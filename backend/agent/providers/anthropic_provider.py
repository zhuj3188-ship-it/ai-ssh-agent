import anthropic
from .base import BaseProvider, AIResponse


class AnthropicProvider(BaseProvider):
    provider_name = "anthropic"

    def __init__(self, config: dict):
        self.client = anthropic.AsyncAnthropic(api_key=config["api_key"])
        self.model = config.get("model", "claude-sonnet-4-20250514")
        self.max_tokens = config.get("max_tokens", 4096)

    async def chat(self, messages, tools=None, system_prompt="", max_tokens=None):
        kwargs = {"model": self.model, "max_tokens": max_tokens or self.max_tokens, "messages": messages}
        if system_prompt: kwargs["system"] = system_prompt
        if tools: kwargs["tools"] = tools
        resp = await self.client.messages.create(**kwargs)
        content = ""
        tool_calls = []
        for block in resp.content:
            if block.type == "text": content += block.text
            elif block.type == "tool_use":
                tool_calls.append({"id": block.id, "name": block.name, "input": block.input})
        return AIResponse(content=content, tool_calls=tool_calls,
            tokens_in=resp.usage.input_tokens, tokens_out=resp.usage.output_tokens,
            model=self.model, provider=self.provider_name, finish_reason=resp.stop_reason)

    def estimate_cost(self, tokens_in, tokens_out):
        return (tokens_in * 3 / 1_000_000) + (tokens_out * 15 / 1_000_000)
