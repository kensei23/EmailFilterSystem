from fastapi import FastAPI
from pydantic import BaseModel
from contextlib import asynccontextmanager
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.naive_bayes import MultinomialNB
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import make_pipeline

# This dictionary will holds trained model in memory
ml_models = {}

# The lifespan function runs one time when the server starts up
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
    
    # Save the trained model to our global dictionary
    ml_models["pipeline"] = pipeline
    print(f"SUCCESS: Model trained on {len(df)} emails and server is ready!")
    
    yield # The server runs here
    
    # Clean up when the server shuts down
    ml_models.clear()

# Create the API server
app = FastAPI(title="Email ML Microservice", lifespan=lifespan)

# Define the exact JSON structure we expect Java to send us
class EmailRequest(BaseModel):
    subject: str
    body: str

# Define the endpoint that Java will hit
@app.post("/predict")
async def predict_email(request: EmailRequest):
    # Combine the incoming text exactly like we did during training
    combined_text = request.subject + " " + request.body
    
    # Grab the trained model
    pipeline = ml_models["pipeline"]
    
    # Make the prediction
    prediction = pipeline.predict([combined_text])[0]
    
    # Calculate how confident the AI is (0.0 to 1.0)
    probabilities = pipeline.predict_proba([combined_text])[0]
    confidence = max(probabilities)
    
    # Return a JSON response back to Java
    return {
        "category": str(prediction),
        "confidence": round(float(confidence), 4)
    }