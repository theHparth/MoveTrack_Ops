import os
import joblib
import pandas as pd

MODEL_PATH = os.path.join("model", "triage_model.joblib")


def test_model_loads():
    model = joblib.load(MODEL_PATH)
    assert model is not None


def test_model_predicts_known_priority():
    model = joblib.load(MODEL_PATH)
    row = pd.DataFrame([{
        "claim_amount": 9500,
        "days_since_incident": 70,
        "claim_type": "vehicle_damage",
        "description_length": 200,
    }])
    prediction = model.predict(row)[0]
    assert prediction in ("low", "medium", "high")