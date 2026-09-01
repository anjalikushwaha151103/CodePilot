from enum import Enum
from pydantic import BaseModel, Field
from typing import List, Optional, Any

class Language(str, Enum):
    CPP = "cpp"
    JAVA = "java"
    PYTHON = "python"
    JAVASCRIPT = "javascript"

class HintLevel(int, Enum):
    SOCRATIC_PROBE = 0
    CONCEPT_NUDGE = 1
    DIRECTIONAL_STRATEGY = 2
    ALGORITHMIC_BLUEPRINT = 3
    FULL_SOLUTION = 4

class ProblemContext(BaseModel):
    platform: str
    problemId: Optional[str] = None
    title: Optional[str] = None
    url: Optional[str] = None
    description: Optional[str] = None
    constraints: Optional[List[str]] = None
    examples: Optional[List[str]] = None
    tags: Optional[List[str]] = None
    difficulty: Optional[str] = None
    source: Optional[str] = None

class CodeAnalysisRequest(BaseModel):
    language: Language
    code: str = Field(..., max_length=50000)
    problemContext: Optional[ProblemContext] = None

class CodeAnalysisResult(BaseModel):
    language: str
    parseSuccessful: bool
    complexityEstimate: str
    complexityConfidence: float
    recursionDetected: bool
    maxLoopDepth: int
    functionCount: int
    branchCount: int
    issues: List[str]
    strengths: List[str]
    evidence: List[str]

class TutoringRequest(BaseModel):
    language: Language
    code: str = Field(..., max_length=50000)
    problemContext: Optional[ProblemContext] = None
    hintLevel: HintLevel = HintLevel.SOCRATIC_PROBE
    userQuestion: Optional[str] = None

class TutoringResponse(BaseModel):
    hintLevel: HintLevel
    message: str
    concept: Optional[str] = None
    confidence: float
    shouldRevealSolution: bool
