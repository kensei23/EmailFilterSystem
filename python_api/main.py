from fastapi import FastAPI
from pydantic import BaseModel
from contextlib import asynccontextmanager
import pandas as pd
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

client = genai.Client()

class DraftRequest(BaseModel):
    email_body: str
    category: str

# This function runs one time when the server starts up
@asynccontextmanager
async def lifespan(app: FastAPI):
    print("Loading data and training the Machine Learning model...")
    
    # Loading the dataset
    df = pd.read_csv("final_training_data.csv")
    df['subject'] = df['subject'].fillna('')
    df['email_body'] = df['email_body'].fillna('')
    
    df['Category'] = df['Category'].replace({
        'Interview': 'Action Needed', 
        'Action Required': 'Action Needed'
    })
    
    # Combine subject and body for the model to read
    X = df['subject'] + " " + df['email_body']
    y = df['Category']
    
    # Build and train the pipeline
    # TfidfVectorizer: Converts words to numbers based on importance (ignores 'the', 'and', etc.)
    # MultinomialNB: The standard Naive Bayes algorithm for text classification
    pipeline = make_pipeline(
        TfidfVectorizer(stop_words='english'), 
        LogisticRegression(class_weight='balanced', max_iter=1000)
    )
    pipeline.fit(X, y)
    
    # Save the trained model to dictionary
    ml_models["pipeline"] = pipeline
    print(f"SUCCESS: Model trained on {len(df)} emails and server is ready!")
    
    yield # The server runs here
    
    # Clean up when the server shuts down
    ml_models.clear()

# Create the API server
app = FastAPI(title="Email ML Microservice", lifespan=lifespan)

# Define the JSON structure we expect Java to send us
class EmailRequest(BaseModel):
    subject: str
    body: str

# Define the endpoint that Java will hit
@app.post("/predict")
async def predict_email(request: EmailRequest):
    combined_text = request.subject + " " + request.body
    
    # Grab the trained model
    pipeline = ml_models["pipeline"]

    prediction = pipeline.predict([combined_text])[0]
    
    # Calculate how confident the AI is (0.0 to 1.0)
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
         # Makes AI call
         response = client.models.generate_content(
             model='gemini-2.5-flash',
             contents =full_prompt
         )
         return {"draft": response.text.strip()}
    except Exception as e:
        return {"draft": f"Error generating reply: {str(e)}"}