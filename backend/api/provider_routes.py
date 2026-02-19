from fastapi import APIRouter, Depends
from pydantic import BaseModel
from typing import Optional
from api.auth import get_current_user

router = APIRouter(prefix="/api/providers", tags=["providers"])


class ValidateRequest(BaseModel):
    provider: str
    api_key: str
    base_url: Optional[str] = None


@router.post("/validate")
async def validate_provider(body: ValidateRequest, user=Depends(get_current_user)):
    """Test if an API key is valid for the given provider."""
    from agent.providers import PROVIDER_MAP
    cls = PROVIDER_MAP.get(body.provider)
    if not cls:
        return {"valid": False, "error": f"Unknown provider: {body.provider}"}
    try:
        config = {"api_key": body.api_key, "max_tokens": 50}
        if body.base_url:
            config["base_url"] = body.base_url
        provider = cls(config)
        resp = await provider.chat([{"role": "user", "content": "hi"}], max_tokens=5)
        return {"valid": True, "model": resp.model, "provider": body.provider}
    except Exception as e:
        return {"valid": False, "error": str(e)[:200]}


@router.get("/list")
async def list_providers(user=Depends(get_current_user)):
    from server import brain
    available = list(brain.providers.keys())
    all_providers = ["anthropic", "openai", "gemini", "deepseek", "zhipu", "nvidia", "ollama"]
    return {"available": available, "all": all_providers, "default": brain.default_provider}
