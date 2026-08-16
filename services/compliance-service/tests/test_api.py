import mongomock
from fastapi.testclient import TestClient

from app import db as db_module
from app.main import app

_fake_client = mongomock.MongoClient()


def fake_get_db():
    return _fake_client["compliance"]


def test_post_and_get_findings(monkeypatch):
    monkeypatch.setattr(db_module, "get_db", fake_get_db)
    client = TestClient(app)

    response = client.post(
        "/findings",
        json={"source": "trivy", "severity": "LOW", "title": "CVE-TEST", "target": "shipment-ingestion"},
    )
    assert response.status_code == 200

    response = client.get("/findings")
    assert response.status_code == 200
    assert len(response.json()) == 1