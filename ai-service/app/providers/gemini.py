import json
import google.generativeai as genai
from app.providers.base import LLMProvider
from app.api.models import TutoringRequest, TutoringResponse, CodeAnalysisResult
from app.core.config import settings
from fastapi import HTTPException

class GeminiProvider(LLMProvider):
    def __init__(self):
        if not settings.GEMINI_API_KEY:
            raise ValueError("GEMINI_API_KEY is missing but Gemini provider is selected.")
        genai.configure(api_key=settings.GEMINI_API_KEY)
        self.model = genai.GenerativeModel(settings.GEMINI_MODEL)

    def generate(self, request: TutoringRequest, analysis: CodeAnalysisResult, system_prompt: str) -> TutoringResponse:
        
        prompt = f"""
{system_prompt}

### CODE ANALYSIS ###
Language: {analysis.language}
Complexity Estimate: {analysis.complexityEstimate}
Issues: {', '.join(analysis.issues) if analysis.issues else 'None'}
Strengths: {', '.join(analysis.strengths) if analysis.strengths else 'None'}

### STUDENT CODE ###
```{analysis.language}
{request.code}
```

### PROBLEM CONTEXT ###
Title: {request.problemContext.title if request.problemContext else 'Unknown'}
Description: {request.problemContext.description if request.problemContext else 'Unknown'}

### USER QUESTION ###
{request.userQuestion or "No explicit question. Please provide the next hint."}

### INSTRUCTIONS ###
Respond ONLY in valid JSON matching this schema:
{{
  "hintLevel": {request.hintLevel.value},
  "message": "String: Your hint or explanation",
  "concept": "String: Core algorithmic concept (e.g. 'Two Pointers')",
  "confidence": "Float: 0.0 to 1.0",
  "shouldRevealSolution": "Boolean"
}}
"""
        
        try:
            # We enforce JSON structure from the LLM if possible, but manually parse since raw GenAI text API is used here.
            # Using basic generate_content
            # To handle timeouts, GenAI client doesn't have an easy direct timeout kwargs in all versions, 
            # but we simulate basic structure parsing.
            response = self.model.generate_content(prompt) # In production we would add generation_config with JSON schema
            
            # Simple cleanup for markdown json blocks if any
            text = response.text
            if text.startswith("```json"):
                text = text.replace("```json", "", 1)
                if text.endswith("```"):
                    text = text[:-3]
            elif text.startswith("```"):
                text = text.replace("```", "", 1)
                if text.endswith("```"):
                    text = text[:-3]
                    
            data = json.loads(text.strip())
            
            return TutoringResponse(**data)
            
        except json.JSONDecodeError:
            raise ValueError("Malformed response from Gemini provider (invalid JSON)")
        except Exception as e:
            raise HTTPException(status_code=502, detail=f"Gemini API Error: {str(e)}")
