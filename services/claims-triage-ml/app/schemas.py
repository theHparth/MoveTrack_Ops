from pydantic import BaseModel, Field
from typing import Literal


class ClaimFeatures(BaseModel):
    claim_amount: float = Field(..., gt=0)
    days_since_incident: int = Field(..., ge=0)
    claim_type: Literal["household_goods", "vehicle_damage", "personal_property", "temporary_lodging"]
    description_length: int = Field(..., ge=0)


class TriageResponse(BaseModel):
    priority: str
    cached: bool