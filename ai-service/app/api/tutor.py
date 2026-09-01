from fastapi import APIRouter
from app.api.models import TutoringRequest, TutoringResponse
from app.tutoring.orchestrator import TutoringOrchestrator

router = APIRouter()

@router.post("/tutor", response_model=TutoringResponse)
async def tutor(request: TutoringRequest):
    return await TutoringOrchestrator.process_request(request)
