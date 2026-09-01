from datetime import datetime, timezone
from fastapi import APIRouter
from pydantic import BaseModel

router = APIRouter()

class HealthResponse(BaseModel):
    status: str
    service: str
    version: str
    timestamp: str

@router.get("/health", response_model=HealthResponse)
async def get_health():
    return HealthResponse(
        status="UP",
        service="codepilot-ai-service",
        version="1.0.0-MVP",
        timestamp=datetime.now(timezone.utc).isoformat()
    )
