from datetime import datetime, timezone

from fastapi import FastAPI

from app import db as db_module
from app.logic import should_publish
from app.schemas import Finding
from app.events import publish_finding_detected

app = FastAPI(title="Compliance Service")


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/findings")
def create_finding(finding: Finding):
    db = db_module.get_db()
    doc = finding.model_dump()
    doc["detected_at"] = doc.get("detected_at") or datetime.now(timezone.utc)
    result = db.findings.insert_one(doc)
    if should_publish(doc["severity"]):
        publish_finding_detected(doc)
    return {"id": str(result.inserted_id)}

@app.get("/findings")
def list_findings():
    db = db_module.get_db()
    docs = []
    for doc in db.findings.find():
        doc["_id"] = str(doc["_id"])
        docs.append(doc)
    return docs