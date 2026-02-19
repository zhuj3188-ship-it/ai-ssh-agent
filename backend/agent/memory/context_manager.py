import time
import json


class ContextManager:
    """管理对话上下文，支持多会话"""

    def __init__(self, max_history: int = 100):
        self.max_history = max_history
        self.sessions = {}

    def get_or_create(self, session_id: str) -> dict:
        if session_id not in self.sessions:
            self.sessions[session_id] = {
                "messages": [],
                "created_at": time.time(),
                "last_active": time.time(),
                "metadata": {}
            }
        session = self.sessions[session_id]
        session["last_active"] = time.time()
        return session

    def add_message(self, session_id: str, role: str, content: str):
        session = self.get_or_create(session_id)
        session["messages"].append({
            "role": role,
            "content": content,
            "timestamp": time.time()
        })
        if len(session["messages"]) > self.max_history:
            session["messages"] = self._compress(session["messages"])

    def get_messages(self, session_id: str) -> list:
        session = self.get_or_create(session_id)
        return [{"role": m["role"], "content": m["content"]} for m in session["messages"]]

    def clear(self, session_id: str):
        if session_id in self.sessions:
            self.sessions[session_id]["messages"] = []

    def _compress(self, messages: list) -> list:
        if len(messages) <= 50:
            return messages

        old = messages[:-20]
        recent = messages[-20:]

        summary_parts = []
        for m in old[-10:]:
            role = m["role"]
            content = m["content"][:100]
            summary_parts.append(f"[{role}] {content}")

        summary = "\n".join(summary_parts)

        summary_msg = {
            "role": "system",
            "content": f"[Earlier conversation summary]\n{summary}\n... ({len(old)} messages summarized)",
            "timestamp": time.time()
        }

        return [messages[0], summary_msg] + recent

    def cleanup_old_sessions(self, max_age: int = 86400):
        now = time.time()
        expired = [sid for sid, s in self.sessions.items() if now - s["last_active"] > max_age]
        for sid in expired:
            del self.sessions[sid]
