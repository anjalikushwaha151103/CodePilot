import asyncio
from fastapi import HTTPException
from app.api.models import TutoringRequest, TutoringResponse, CodeAnalysisResult
from app.intel.parser import CodeParser
from app.intel.analyzer import ASTAnalyzer
from app.intel.complexity import ComplexityEstimator
from app.guardrails.prompts import build_system_prompt
from app.router.provider_router import get_llm_provider
from app.core.config import settings

class TutoringOrchestrator:
    @staticmethod
    async def process_request(request: TutoringRequest) -> TutoringResponse:
        # 1. Parse Code
        try:
            tree = CodeParser.parse(request.code, request.language)
        except ValueError as e:
            raise HTTPException(status_code=400, detail=str(e))
        except Exception as e:
            raise HTTPException(status_code=500, detail="Failed to parse code structure")

        # 2. Static Analysis
        analyzer = ASTAnalyzer(tree, request.language)
        analyzer.analyze()
        
        # 3. Complexity Heuristics
        heuristics = ComplexityEstimator.estimate(analyzer)
        
        analysis_result = CodeAnalysisResult(
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

        # 4. Guardrails (System Prompt)
        system_prompt = build_system_prompt(request.hintLevel)

        # 5. LLM Provider (with timeout)
        provider = get_llm_provider()
        
        try:
            # We run the provider in a thread pool since some providers (like google generative AI)
            # might have blocking HTTP calls in their standard library.
            # And we wrap it in a timeout.
            response = await asyncio.wait_for(
                asyncio.to_thread(provider.generate, request, analysis_result, system_prompt),
                timeout=settings.LLM_TIMEOUT_SECONDS
            )
            return response
            
        except asyncio.TimeoutError:
            raise HTTPException(status_code=504, detail="LLM Provider timed out")
        except ValueError as e:
            # Handle structured response validation failure
            raise HTTPException(status_code=502, detail=f"LLM Provider response validation failed: {str(e)}")
        except Exception as e:
            # Re-raise HTTPExceptions (e.g. from GeminiProvider)
            if isinstance(e, HTTPException):
                raise e
            raise HTTPException(status_code=500, detail=f"Internal Orchestration Error: {str(e)}")
