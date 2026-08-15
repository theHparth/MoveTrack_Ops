import os
import joblib
import pandas as pd
from fastapi import FastAPI

from app.schemas import ClaimFeatures, TriageResponse
from app.cache import get_cached_prediction, set_cached_prediction

MODEL_PATH = os.path.join("model", "triage_model.joblib")

app = FastAPI(title="Claims Triage ML Service")
model = joblib.load(MODEL_PATH)


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/predict", response_model=TriageResponse)
def predict(features: ClaimFeatures):
    feature_dict = features.model_dump()

    cached = get_cached_prediction(feature_dict)
    if cached is not None:
        return TriageResponse(priority=cached, cached=True)

    row = pd.DataFrame([feature_dict])
    prediction = model.predict(row)[0]
    set_cached_prediction(feature_dict, prediction)
    return TriageResponse(priority=prediction, cached=False)