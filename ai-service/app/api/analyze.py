from fastapi import APIRouter, HTTPException
from app.api.models import CodeAnalysisRequest, CodeAnalysisResult
from app.intel.parser import CodeParser
from app.intel.analyzer import ASTAnalyzer
from app.intel.complexity import ComplexityEstimator

router = APIRouter()

@router.post("/analyze", response_model=CodeAnalysisResult)
async def analyze_code(request: CodeAnalysisRequest):
    try:
        tree = CodeParser.parse(request.code, request.language)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Failed to parse code: {str(e)}")

    analyzer = ASTAnalyzer(tree, request.language)
    analyzer.analyze()
    
    heuristics = ComplexityEstimator.estimate(analyzer)
    
    return CodeAnalysisResult(
        language=request.language.value,
        parseSuccessful=True,
        complexityEstimate=heuristics["complexityEstimate"],
        complexityConfidence=heuristics["complexityConfidence"],
        recursionDetected=analyzer.recursion_detected,
        maxLoopDepth=analyzer.max_loop_depth,
        functionCount=analyzer.function_count,
        branchCount=analyzer.branch_count,
        issues=heuristics["issues"],
        strengths=heuristics["strengths"],
        evidence=heuristics["evidence"]
    )
