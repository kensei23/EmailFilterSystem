import joblib
import pandas as pd
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import classification_report, confusion_matrix
from sklearn.model_selection import train_test_split
from sklearn.pipeline import make_pipeline

MODEL_PATH = "model.pkl"
DATA_PATH = "final_training_data.csv"

def load_and_prepare_data():
    df = pd.read_csv(DATA_PATH)
    df['subject'] = df['subject'].fillna('')
    df['email_body'] = df['email_body'].fillna('')

    df['Category'] = df['Category'].replace({
        'Interview': 'Action Needed',
        'Action Required': 'Action Needed'
    })

    X = df['subject'] + " " + df['email_body']
    y = df['Category']
    return X, y

def build_pipeline():
    return make_pipeline(
        TfidfVectorizer(stop_words='english'),
        LogisticRegression(class_weight='balanced', max_iter=1000)
    )

def main():
    X, y = load_and_prepare_data()
    print(f"Loaded {len(X)} labelled emails across {y.nunique()} categories.")

    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42, stratify=y
    )

    eval_pipeline = build_pipeline()
    eval_pipeline.fit(X_train, y_train)

    predictions = eval_pipeline.predict(X_test)

    print("\n--- Model Evaluation Classification Report ---")
    print(classification_report(y_test, predictions))
    print("Confusion matrix (rows = actual, columns = predicted):")
    print(f"Categories: {sorted(y.unique())}")
    print(confusion_matrix(y_test, predictions, labels=sorted(y.unique())))

    final_pipeline = build_pipeline()
    final_pipeline.fit(X, y)

    joblib.dump(final_pipeline, MODEL_PATH)
    print(f"\nSaved trained model to {MODEL_PATH}")


if __name__ == "__main__":
    main()