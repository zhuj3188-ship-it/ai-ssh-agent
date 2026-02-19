from .base import BaseProvider, AIResponse
from .anthropic_provider import AnthropicProvider
from .openai_provider import OpenAIProvider
from .gemini_provider import GeminiProvider
from .deepseek_provider import DeepSeekProvider
from .zhipu_provider import ZhipuProvider
from .nvidia_provider import NvidiaProvider
from .ollama_provider import OllamaProvider

PROVIDER_MAP = {
    "anthropic": AnthropicProvider, "openai": OpenAIProvider,
    "gemini": GeminiProvider, "deepseek": DeepSeekProvider,
    "zhipu": ZhipuProvider, "nvidia": NvidiaProvider, "ollama": OllamaProvider,
}

def create_provider(name: str, config: dict) -> BaseProvider:
    cls = PROVIDER_MAP.get(name)
    if not cls: raise ValueError(f"Unknown provider: {name}")
    return cls(config)
