from fastapi import FastAPI
from pydantic import BaseModel
from contextlib import asynccontextmanager
import pandas as pd
import joblib
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.naive_bayes import MultinomialNB
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import make_pipeline
from google import genai
import os
from dotenv import load_dotenv

# Loads private variable from env file
load_dotenv()
# api_key = os.getenv("GEMINI_API_KEY")
# if not api_key:
#     print("No Gemini API Key found in .env file.")

# This dictionary holds trained model in memory
ml_models = {}

MODEL_PATH = "model.pkl"

client = genai.Client()

class DraftRequest(BaseModel):
    email_body: str
    category: str

# This function runs one time when the server starts up
@asynccontextmanager
async def lifespan(app: FastAPI):
    print("Loading trained ML model...")

    try:
        pipeline = joblib.load(MODEL_PATH)
        ml_models["pipeline"] = pipeline
        print("SUCCESS: Model loaded and server is ready!")
    except FileNotFoundError:
        print(f"WARNING: {MODEL_PATH} not found. Run train_model.py first.")
        print("The server will still start, but /predict will return an error until a model exists.")

    yield # The server runs here

    ml_models.clear()

# Create the API server
app = FastAPI(title="Email ML Microservice", lifespan=lifespan)

class EmailRequest(BaseModel):
    subject: str
    body: str

# End point Java calls
@app.post("/predict")
async def predict_email(request: EmailRequest):
    if "pipeline" not in ml_models:
        return {"category": "Uncategorised", "confidence": 0.0,
                "error": "Model not loaded - run train_model.py first."}
    
    combined_text = request.subject + " " + request.body

    pipeline = ml_models["pipeline"]

    prediction = pipeline.predict([combined_text])[0]
    
    probabilities = pipeline.predict_proba([combined_text])[0]
    confidence = max(probabilities)
    
    # Return a JSON response back to Java
    return {
        "category": str(prediction),
        "confidence": round(float(confidence), 4)
    }

@app.get("/health")
async def health_check():
    return {"status": "ok", "model_loaded": "pipeline" in ml_models}

@app.post("/api/draft-reply")
def generate_reply(request: DraftRequest):
    print(f"Drafting reply for category: {request.category}")

    # Prompt engineering logic
    if request.category == "Rejection":
        system_prompt = ("You are a professional career assistant. The user recieved a job rejection. "
                        "Write a short 3-sentence reply thanking the recruiter for their time, "
                        "and politely ask for feedback on how to improve for future opportunities. Do not include subject lines"
                        )
    elif request.category == "Action Needed":
        system_prompt = ("You are a professional career assistant. The user received an interview request or next-steps email. "
                        "Write a polite 3-sentence reply expressing excitement for the opportunity and stating that "
                        "they are available for a call next week. Leave placeholders like [Insert Time] for them to fill out. Do not include subject lines."
                        )
    else:
        return {"draft": "No automated reply needed for this category."}
    
    # Combining instructings with email content
    full_prompt = f"{system_prompt}\n\nHere is the email to reply to:\n{request.email_body}"

    try:
         response = client.models.generate_content(
             model='gemini-2.5-flash',
             contents =full_prompt
         )
         return {"draft": response.text.strip()}
    except Exception as e:
        return {"draft": f"Error generating reply: {str(e)}"}