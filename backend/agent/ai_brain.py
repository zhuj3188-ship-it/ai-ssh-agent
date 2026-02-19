import json
import logging
from dataclasses import asdict
from .ssh_executor import SSHExecutor
from .safety import SafetyFilter
from .file_manager import FileManager
from .deployer import Deployer
from .key_manager import KeyManager
from .tool_registry import TOOLS
from .providers import create_provider, AIResponse
from .memory import ContextManager, HabitLearner

logger = logging.getLogger("ai_brain")

SYSTEM_PROMPT = """You are an expert Linux sysadmin AI with direct SSH access to servers.
CAPABILITIES: Execute commands, generate/compress/deploy files, manage servers.
RULES:
1. SAFETY FIRST: Never run destructive commands without confirmation.
2. ANALYZE before acting with read-only commands.
3. If command fails, analyze error and suggest fix.
4. Never output SSH keys, passwords, or API tokens.
5. Respond in the same language the user uses.

{context}
{habits}
"""


class AIBrain:
    def __init__(self, config: dict):
        self.config = config
        self.ssh = SSHExecutor(config)
        self.safety = SafetyFilter(config)
        self.fm = FileManager(config.get("workspace", {}).get("base_dir", "/var/workspace"))
        self.deployer = Deployer(self.ssh, self.fm)
        self.key_manager = KeyManager(
            config.get("ssh", {}).get("key_dir", "/var/ssh-keys"),
            config.get("ssh", {}).get("temp_key_ttl", 3600))
        self.providers = {}
        self.default_provider = config.get("ai", {}).get("default_provider", "anthropic")
        for name, pcfg in config.get("ai", {}).get("providers", {}).items():
            if pcfg.get("api_key") or name == "ollama":
                try: self.providers[name] = create_provider(name, pcfg)
                except Exception as e: logger.warning(f"Failed to init provider {name}: {e}")
        self.context = ContextManager()
        self.habits = HabitLearner()

    async def chat(self, user_message: str, provider_name: str = None, user_id: int = None, session_id: str = "default") -> dict:
        provider_name = provider_name or self.default_provider
        provider = self.providers.get(provider_name)
        if not provider:
            return {"reply": f"Provider {provider_name} not available.", "tokens_in": 0, "tokens_out": 0, "cost": 0}
        self.context.add_message(session_id, "user", user_message)
        system = SYSTEM_PROMPT.format(context="", habits=self.habits.get_habits_prompt())
        messages = self.context.get_messages(session_id)
        tools = TOOLS if provider_name != "gemini" else None
        total_in = 0
        total_out = 0
        for step in range(15):
            resp: AIResponse = await provider.chat(messages, tools=tools, system_prompt=system)
            total_in += resp.tokens_in
            total_out += resp.tokens_out
            if not resp.tool_calls:
                self.context.add_message(session_id, "assistant", resp.content)
                cost = provider.estimate_cost(total_in, total_out)
                return {"reply": resp.content, "tokens_in": total_in, "tokens_out": total_out,
                        "cost": cost, "provider": provider_name, "model": resp.model}
            if provider_name == "anthropic":
                messages.append({"role": "assistant", "content": resp.tool_calls})
            else:
                messages.append({"role": "assistant", "content": resp.content, "tool_calls": resp.tool_calls})
            for tc in resp.tool_calls:
                result = self._process_tool(tc["name"], tc["input"])
                if provider_name == "anthropic":
                    messages.append({"role": "user", "content": [
                        {"type": "tool_result", "tool_use_id": tc["id"], "content": json.dumps(result, default=str)}]})
                else:
                    messages.append({"role": "tool", "tool_call_id": tc["id"], "content": json.dumps(result, default=str)})
        return {"reply": "Reached maximum steps.", "tokens_in": total_in, "tokens_out": total_out, "cost": 0}

    def _process_tool(self, name: str, args: dict) -> dict:
        try:
            if name == "ssh_execute":
                check = self.safety.check(args["command"])
                if not check.safe: return {"error": check.reason, "blocked": True}
                if check.needs_confirmation: return {"needs_confirmation": True, "command": args["command"], "reason": check.reason}
                result = self.ssh.execute(args["server"], args["command"])
                self.habits.record_command(args["command"])
                if not result.success: self.habits.record_error(args["command"], result.stderr, "")
                self.context.update_server(args["server"], {"last_cmd": args["command"], "ok": result.success})
                return asdict(result)
            elif name == "list_servers": return {"servers": self.ssh.list_servers()}
            elif name == "test_connection": return self.ssh.test_connection(args["server"])
            elif name == "generate_file":
                path = self.fm.generate_file(args["filename"], args["content"], args.get("subdir", ""))
                return {"path": path, "size": len(args["content"])}
            elif name == "generate_project":
                path = self.fm.generate_project(args["project_name"], args["files"])
                return {"path": path, "files_count": len(args["files"])}
            elif name == "compress_file": return {"archive": self.fm.compress(args["source"], args.get("format", "tar.gz"))}
            elif name == "extract_file": return {"extracted_to": self.fm.extract(args["archive"], args.get("dest", ""))}
            elif name == "list_workspace": return {"items": self.fm.list_workspace(args.get("subdir", ""))}
            elif name == "file_upload": return {"success": self.ssh.upload(args["server"], args["local_path"], args["remote_path"])}
            elif name == "file_download": return {"success": self.ssh.download(args["server"], args["remote_path"], args["local_path"])}
            elif name == "deploy_file": return self.deployer.deploy_file(args["server"], args["filename"], args["content"], args["remote_path"])
            elif name == "deploy_project": return self.deployer.deploy_project(args["server"], args["project_name"], args["files"], args["remote_dir"], args.get("setup_command", ""))
            else: return {"error": f"Unknown tool: {name}"}
        except Exception as e:
            logger.exception(f"Tool {name} failed")
            return {"error": str(e)}

    def reset(self):
        self.context.reset()
