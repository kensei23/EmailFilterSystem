# EmailFilterSystem

A desktop email client that classifies incoming emails (*Rejection*, *Action Needed*, *Offer*, *Confirmation*) using a machine learning model, and drafts AI-generated replies for the ones that need a response.

Java Swing app + Python ML microservice, connecting to real inboxes via IMAP with OAuth2 (Outlook) or app-password (iCloud) login.

## Features

- OAuth2 login for Outlook (MSAL) — no plaintext password for Outlook accounts.
- App-password login for iCloud, encrypted at rest (AES-256-GCM), not stored in plaintext.
- Email classification via a TF-IDF + Logistic Regression model, served over a local REST API.
- AI-drafted replies (Gemini API) for *Rejection*/*Action Needed* emails.
- Searchable, filterable inbox table.

## Tech Stack

Java 21, Maven, Jakarta Mail, MSAL4J, SQLite, Gson · Python 3, FastAPI, scikit-learn, pandas, Google Gemini API

## Setup

**1. Java app** — copy `config.example.properties` to `config.properties` and fill in your [Azure app registration](https://portal.azure.com) details (client ID, authority, scope) for Outlook OAuth. Then:

```bash
mvn clean compile
mvn exec:java
```

**2. Python ML service** — the Java app expects this running on `localhost:8000`:

```bash
cd python_api
pip install -r requirements.txt
uvicorn main:app --reload
```

Add a `.env` file in `python_api/` with `GEMINI_API_KEY=your_key_here`.

`config.properties` and the auto-generated `secret.key` are both gitignored — never commit either.

## Usage

Start the Python service, then launch the Java app. First run prompts you to pick a provider and log in; emails load automatically, tagged by category. Select a *Rejection*/*Action Needed* email and click the AI reply button for a drafted response.

## Security Notes

Outlook uses OAuth2, so the app never sees your Microsoft password. iCloud app passwords are encrypted (AES-256-GCM) before being written to SQLite, using a key generated on first run.