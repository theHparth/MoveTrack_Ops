from datetime import datetime
from typing import Optional
from pydantic import BaseModel


class Finding(BaseModel):
    source: str            # "trivy" or "zap"
    severity: str           # "LOW" / "MEDIUM" / "HIGH" / "CRITICAL"
    title: str
    description: Optional[str] = None
    target: str              # image name or URL that was scanned
    detected_at: Optional[datetime] = None