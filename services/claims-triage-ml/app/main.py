import os
import joblib
import pandas as pd
from fastapi import FastAPI

from app.schemas import ClaimFeatures, TriageResponse

MODEL_PATH = os.path.join("model", "triage_model.joblib")

app = FastAPI(title="Claims Triage ML Service")
model = joblib.load(MODEL_PATH)


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/predict", response_model=TriageResponse)
def predict(features: ClaimFeatures):
    row = pd.DataFrame([features.model_dump()])
    prediction = model.predict(row)[0]
    return TriageResponse(priority=prediction, cached=False)