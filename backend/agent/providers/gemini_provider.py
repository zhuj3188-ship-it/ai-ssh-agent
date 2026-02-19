import google.generativeai as genai
from .base import BaseProvider, AIResponse


class GeminiProvider(BaseProvider):
    provider_name = "gemini"

    def __init__(self, config: dict):
        genai.configure(api_key=config["api_key"])
        self.model_name = config.get("model", "gemini-2.0-flash")
        self.max_tokens = config.get("max_tokens", 4096)

    async def chat(self, messages, tools=None, system_prompt="", max_tokens=None):
        model = genai.GenerativeModel(model_name=self.model_name, system_instruction=system_prompt or None)
        history = []
        for m in messages[:-1]:
            role = "user" if m["role"] == "user" else "model"
            history.append({"role": role, "parts": [m["content"]]})
        chat = model.start_chat(history=history)
        last_msg = messages[-1]["content"] if messages else ""
        resp = await chat.send_message_async(last_msg)
        return AIResponse(content=resp.text, tool_calls=[],
            tokens_in=resp.usage_metadata.prompt_token_count if hasattr(resp, "usage_metadata") else 0,
            tokens_out=resp.usage_metadata.candidates_token_count if hasattr(resp, "usage_metadata") else 0,
            model=self.model_name, provider=self.provider_name, finish_reason="stop")

    def estimate_cost(self, tokens_in, tokens_out):
        return (tokens_in * 0.075 / 1_000_000) + (tokens_out * 0.3 / 1_000_000)
