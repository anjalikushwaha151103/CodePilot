from fastapi import APIRouter
from app.core.config import settings
from datetime import datetime, timezone

router = APIRouter()

@router.get("/health")
async def health_check():
    return {
        "status": "UP",
        "service": "codepilot-ai-service",
        "version": settings.VERSION,
        "timestamp": datetime.now(timezone.utc).isoformat(),
    }

@router.get("/readiness")
async def readiness_check():
    checks = {}
    try:
        from app.intel.parser import CodeParser
        parser = CodeParser()
        # Verify at least one grammar loads
        test_result = parser.parse("x = 1", "python")
        checks["tree_sitter"] = "OK" if test_result else "FAIL"
    except Exception as e:
        checks["tree_sitter"] = f"FAIL: {str(e)}"
    
    checks["llm_provider"] = settings.LLM_PROVIDER
    all_ok = all(v == "OK" or v in ["mock", "gemini"] for v in checks.values())
    
    return {
        "status": "READY" if all_ok else "NOT_READY",
        "checks": checks,
    }
