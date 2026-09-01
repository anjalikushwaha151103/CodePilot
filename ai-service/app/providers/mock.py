from app.providers.base import LLMProvider
from app.api.models import TutoringRequest, TutoringResponse, CodeAnalysisResult, HintLevel

class MockLLMProvider(LLMProvider):
    def generate(self, request: TutoringRequest, analysis: CodeAnalysisResult, system_prompt: str) -> TutoringResponse:
        # For unit tests, respond deterministically
        if "timeout" in request.code:
            raise TimeoutError("Mock provider timeout")
            
        if "malformed" in request.code:
            raise ValueError("Malformed mock response")
            
        return TutoringResponse(
            hintLevel=request.hintLevel,
            message="This is a mock tutoring response.",
            concept="mock-concept",
            confidence=0.99,
            shouldRevealSolution=(request.hintLevel == HintLevel.FULL_SOLUTION)
        )
