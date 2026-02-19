import json
import time
from pathlib import Path
from collections import Counter


class HabitLearner:
    def __init__(self, persist_path: str = "/var/workspace/.habits.json"):
        self.persist_path = Path(persist_path)
        self.command_freq: Counter = Counter()
        self.error_lessons: list[dict] = []
        self.preferences: dict[str, str] = {}
        self._load()

    def record_command(self, command: str):
        self.command_freq[command.strip()] += 1
        self._save()

    def record_error(self, command: str, error: str, fix: str):
        self.error_lessons.append({"command": command, "error": error, "fix": fix, "time": time.time()})
        if len(self.error_lessons) > 200:
            self.error_lessons = self.error_lessons[-100:]
        self._save()

    def record_preference(self, key: str, value: str):
        self.preferences[key] = value
        self._save()

    def get_habits_prompt(self) -> str:
        parts = []
        top = self.command_freq.most_common(10)
        if top:
            parts.append("Frequent commands: " + ", ".join(f"`{c}` ({n}x)" for c, n in top))
        recent_errors = self.error_lessons[-5:]
        if recent_errors:
            lessons = "; ".join(f"{e['command']} caused {e['error'][:60]}" for e in recent_errors)
            parts.append(f"Error lessons: {lessons}")
        if self.preferences:
            parts.append(f"Preferences: {json.dumps(self.preferences)}")
        return "\n".join(parts) if parts else ""

    def _save(self):
        data = {"command_freq": dict(self.command_freq),
                "error_lessons": self.error_lessons, "preferences": self.preferences}
        self.persist_path.parent.mkdir(parents=True, exist_ok=True)
        self.persist_path.write_text(json.dumps(data, ensure_ascii=False, indent=2))

    def _load(self):
        if self.persist_path.exists():
            try:
                data = json.loads(self.persist_path.read_text())
                self.command_freq = Counter(data.get("command_freq", {}))
                self.error_lessons = data.get("error_lessons", [])
                self.preferences = data.get("preferences", {})
            except: pass
