from abc import ABC, abstractmethod
from dataclasses import dataclass, field


@dataclass
class AIResponse:
    content: str = ""
    tool_calls: list = field(default_factory=list)
    tokens_in: int = 0
    tokens_out: int = 0
    model: str = ""
    provider: str = ""
    finish_reason: str = ""


class BaseProvider(ABC):
    provider_name: str = ""

    @abstractmethod
    async def chat(self, messages, tools=None, system_prompt="", max_tokens=4096) -> AIResponse: ...

    def estimate_cost(self, tokens_in: int, tokens_out: int) -> float:
        return 0.0
