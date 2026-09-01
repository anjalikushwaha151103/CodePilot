from app.core.config import settings
from app.providers.base import LLMProvider
from app.providers.mock import MockLLMProvider

# Lazily initialized provider
_provider: LLMProvider | None = None

def get_llm_provider() -> LLMProvider:
    global _provider
    if _provider is not None:
        return _provider

    provider_name = settings.LLM_PROVIDER.lower()
    
    if provider_name == "mock":
        _provider = MockLLMProvider()
    elif provider_name == "gemini":
        from app.providers.gemini import GeminiProvider
        _provider = GeminiProvider()
    else:
        raise ValueError(f"Unknown LLM_PROVIDER: {provider_name}")
        
    return _provider
