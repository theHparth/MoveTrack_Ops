from fastapi.testclient import TestClient
from app.main import app

client = TestClient(app)


def test_health_check():
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json() == {"status": "ok"}


def test_predict_happy_path():
    response = client.post("/predict", json={
        "claim_amount": 9500,
        "days_since_incident": 70,
        "claim_type": "vehicle_damage",
        "description_length": 200,
    })
    assert response.status_code == 200
    assert response.json()["priority"] in ("low", "medium", "high")


def test_predict_missing_field_returns_422():
    response = client.post("/predict", json={
        "claim_amount": 9500,
        "claim_type": "vehicle_damage",
    })
    assert response.status_code == 422