import re
from dataclasses import dataclass


@dataclass
class SafetyResult:
    safe: bool
    needs_confirmation: bool
    reason: str
    severity: str


class SafetyFilter:
    INJECTION_PATTERNS = [
        r";\s*rm\s", r"\|\s*bash", r"`[^`]+`", r"\$\([^)]+\)",
        r">\s*/dev/sd", r"curl\s.*\|\s*sh", r"wget\s.*\|\s*bash",
        r"python\s*-c\s*['\x22].*import\s+os", r"nc\s+-[elp]",
    ]

    def __init__(self, config: dict):
        safety_cfg = config.get("safety", {})
        self.blocked = safety_cfg.get("blocked_commands", [])
        self.confirm_keywords = safety_cfg.get("require_confirmation", [])
        self.max_len = safety_cfg.get("max_command_length", 2000)
        self._compiled = [re.compile(p, re.IGNORECASE) for p in self.INJECTION_PATTERNS]

    def check(self, command: str) -> SafetyResult:
        cmd = command.strip()
        if len(cmd) > self.max_len:
            return SafetyResult(False, False, "Command exceeds max length", "medium")
        for blocked in self.blocked:
            if blocked in cmd:
                return SafetyResult(False, False, f"Blocked: contains {blocked}", "critical")
        for pattern in self._compiled:
            if pattern.search(cmd):
                return SafetyResult(False, False, f"Injection pattern detected", "high")
        for kw in self.confirm_keywords:
            if kw in cmd:
                return SafetyResult(True, True, f"Needs confirmation: contains {kw}", "low")
        return SafetyResult(True, False, "OK", "none")
