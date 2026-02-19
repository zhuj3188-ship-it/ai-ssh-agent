import json


class ContextCompressor:
    """压缩过长的对话上下文"""

    def __init__(self, max_messages: int = 50, summary_threshold: int = 40):
        self.max_messages = max_messages
        self.summary_threshold = summary_threshold

    def compress(self, messages: list) -> list:
        if len(messages) <= self.summary_threshold:
            return messages

        old = messages[:-20]
        recent = messages[-20:]

        summary_parts = []
        for m in old[-10:]:
            role = m['role']
            content = m['content'][:100]
            summary_parts.append(f"[{role}] {content}")

        summary = "\n".join(summary_parts)

        summary_msg = {
            "role": "system",
            "content": f"[Earlier conversation summary]\n{summary}\n... ({len(old)} messages summarized)"
        }

        return [messages[0], summary_msg] + recent

    def estimate_tokens(self, messages: list) -> int:
        total = 0
        for m in messages:
            total += len(m.get('content', '')) // 3
        return total
