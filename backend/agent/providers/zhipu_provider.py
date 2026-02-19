import json as _json
import openai
from .base import BaseProvider, AIResponse


class ZhipuProvider(BaseProvider):
    provider_name = "zhipu"

    def __init__(self, config: dict):
        self.client = openai.AsyncOpenAI(api_key=config["api_key"],
            base_url=config.get("base_url", "https://api.z.ai/api/paas/v4/"))
        self.model = config.get("model", "glm-4.7")
        self.max_tokens = config.get("max_tokens", 4096)

    async def chat(self, messages, tools=None, system_prompt="", max_tokens=None):
        msgs = []
        if system_prompt: msgs.append({"role": "system", "content": system_prompt})
        msgs.extend(messages)
        kwargs = {"model": self.model, "messages": msgs, "max_tokens": max_tokens or self.max_tokens}
        if tools: kwargs["tools"] = [{"type": "function", "function": t} for t in tools]
        resp = await self.client.chat.completions.create(**kwargs)
        choice = resp.choices[0]
        tool_calls = []
        if choice.message.tool_calls:
            for tc in choice.message.tool_calls:
                tool_calls.append({"id": tc.id, "name": tc.function.name, "input": _json.loads(tc.function.arguments)})
        return AIResponse(content=choice.message.content or "", tool_calls=tool_calls,
            tokens_in=resp.usage.prompt_tokens if resp.usage else 0,
            tokens_out=resp.usage.completion_tokens if resp.usage else 0,
            model=self.model, provider=self.provider_name, finish_reason=choice.finish_reason)

    def estimate_cost(self, tokens_in, tokens_out):
        return (tokens_in * 0.5 / 1_000_000) + (tokens_out * 1.0 / 1_000_000)
