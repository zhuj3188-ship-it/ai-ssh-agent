import time
import threading
from dataclasses import dataclass
import paramiko


@dataclass
class SSHResult:
    stdout: str = ""
    stderr: str = ""
    exit_code: int = -1
    duration: float = 0.0
    success: bool = False
    server: str = ""
    command: str = ""


class SSHExecutor:
    def __init__(self, config: dict):
        self.servers = config.get("ssh", {}).get("servers", {})
        self.default_timeout = config.get("ssh", {}).get("default_timeout", 30)
        self._pool: dict[str, paramiko.SSHClient] = {}
        self._lock = threading.Lock()

    def _get_connection(self, server_name: str) -> paramiko.SSHClient:
        with self._lock:
            if server_name in self._pool:
                transport = self._pool[server_name].get_transport()
                if transport and transport.is_active():
                    return self._pool[server_name]
                else:
                    self._pool.pop(server_name, None)
            srv = self.servers.get(server_name)
            if not srv:
                raise ValueError(f"Unknown server: {server_name}")
            client = paramiko.SSHClient()
            client.set_missing_host_key_policy(paramiko.AutoAddPolicy())
            connect_kwargs = {
                "hostname": srv["host"],
                "port": srv.get("port", 22),
                "username": srv.get("username", "root"),
                "timeout": self.default_timeout,
            }
            auth = srv.get("auth_method", "key")
            if auth == "key" and srv.get("key_file"):
                connect_kwargs["key_filename"] = srv["key_file"]
            elif auth == "password" and srv.get("password"):
                connect_kwargs["password"] = srv["password"]
            client.connect(**connect_kwargs)
            self._pool[server_name] = client
            return client

    def execute(self, server_name: str, command: str, timeout: int = None) -> SSHResult:
        t = timeout or self.default_timeout
        start = time.time()
        try:
            client = self._get_connection(server_name)
            stdin, stdout, stderr = client.exec_command(command, timeout=t)
            exit_code = stdout.channel.recv_exit_status()
            return SSHResult(
                stdout=stdout.read().decode("utf-8", errors="replace"),
                stderr=stderr.read().decode("utf-8", errors="replace"),
                exit_code=exit_code,
                duration=round(time.time() - start, 3),
                success=(exit_code == 0),
                server=server_name, command=command,
            )
        except Exception as e:
            return SSHResult(stderr=str(e), exit_code=-1,
                duration=round(time.time() - start, 3),
                success=False, server=server_name, command=command)

    def upload(self, server_name: str, local_path: str, remote_path: str) -> bool:
        try:
            client = self._get_connection(server_name)
            sftp = client.open_sftp()
            sftp.put(local_path, remote_path)
            sftp.close()
            return True
        except Exception:
            return False

    def download(self, server_name: str, remote_path: str, local_path: str) -> bool:
        try:
            client = self._get_connection(server_name)
            sftp = client.open_sftp()
            sftp.get(remote_path, local_path)
            sftp.close()
            return True
        except Exception:
            return False

    def test_connection(self, server_name: str) -> dict:
        result = self.execute(server_name, "echo ok")
        return {"server": server_name, "connected": result.success,
                "latency_ms": int(result.duration * 1000),
                "error": result.stderr if not result.success else None}

    def list_servers(self) -> list[dict]:
        out = []
        for name, srv in self.servers.items():
            out.append({"name": name, "host": srv["host"],
                        "port": srv.get("port", 22), "username": srv.get("username", "root")})
        return out

    def close_all(self):
        with self._lock:
            for client in self._pool.values():
                try: client.close()
                except: pass
            self._pool.clear()
