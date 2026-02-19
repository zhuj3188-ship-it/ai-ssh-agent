import openai
from .base import BaseProvider, AIResponse


class OllamaProvider(BaseProvider):
    provider_name = "ollama"

    def __init__(self, config: dict):
        self.client = openai.AsyncOpenAI(api_key="ollama",
            base_url=config.get("base_url", "http://localhost:11434/v1"))
        self.model = config.get("model", "llama3.2")
        self.max_tokens = config.get("max_tokens", 4096)

    async def chat(self, messages, tools=None, system_prompt="", max_tokens=None):
        msgs = []
        if system_prompt: msgs.append({"role": "system", "content": system_prompt})
        msgs.extend(messages)
        kwargs = {"model": self.model, "messages": msgs, "max_tokens": max_tokens or self.max_tokens}
        resp = await self.client.chat.completions.create(**kwargs)
        choice = resp.choices[0]
        return AIResponse(content=choice.message.content or "",
            tokens_in=resp.usage.prompt_tokens if resp.usage else 0,
            tokens_out=resp.usage.completion_tokens if resp.usage else 0,
            model=self.model, provider=self.provider_name, finish_reason=choice.finish_reason)

    def estimate_cost(self, tokens_in, tokens_out): return 0.0
