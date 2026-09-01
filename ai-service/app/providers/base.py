from abc import ABC, abstractmethod
from typing import Optional
from app.api.models import TutoringRequest, TutoringResponse, CodeAnalysisResult

class LLMProvider(ABC):
    @abstractmethod
    def generate(self, request: TutoringRequest, analysis: CodeAnalysisResult, system_prompt: str) -> TutoringResponse:
        """
        Generate a structured TutoringResponse based on the request, analysis, and system prompt.
        """
        pass
