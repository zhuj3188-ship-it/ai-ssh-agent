import os
import stat
import time
import secrets
import threading
from pathlib import Path
from cryptography.hazmat.primitives import serialization
from cryptography.hazmat.primitives.asymmetric import ed25519


class KeyManager:
    def __init__(self, key_dir: str = "/var/ssh-keys", temp_ttl: int = 3600):
        self.key_dir = Path(key_dir)
        self.key_dir.mkdir(parents=True, exist_ok=True)
        os.chmod(self.key_dir, stat.S_IRWXU)
        self.temp_ttl = temp_ttl
        self._temp_keys: dict[str, float] = {}
        self._start_cleanup()

    def generate_temp_key(self, label: str = "") -> dict:
        private_key = ed25519.Ed25519PrivateKey.generate()
        key_id = secrets.token_hex(8)
        name = f"temp_{label}_{key_id}" if label else f"temp_{key_id}"
        priv_path = self.key_dir / name
        pub_path = self.key_dir / f"{name}.pub"
        priv_bytes = private_key.private_bytes(
            serialization.Encoding.PEM,
            serialization.PrivateFormat.OpenSSH,
            serialization.NoEncryption(),
        )
        pub_bytes = private_key.public_key().public_bytes(
            serialization.Encoding.OpenSSH,
            serialization.PublicFormat.OpenSSH,
        )
        priv_path.write_bytes(priv_bytes)
        pub_path.write_bytes(pub_bytes)
        os.chmod(priv_path, stat.S_IRUSR | stat.S_IWUSR)
        os.chmod(pub_path, stat.S_IRUSR | stat.S_IWUSR)
        self._temp_keys[str(priv_path)] = time.time() + self.temp_ttl
        return {"key_id": key_id, "private_path": str(priv_path),
                "public_key": pub_bytes.decode(), "expires_in": self.temp_ttl}

    def revoke(self, key_id: str):
        for f in self.key_dir.glob(f"*{key_id}*"):
            f.unlink(missing_ok=True)

    def cleanup_expired(self) -> int:
        now = time.time()
        expired = [p for p, exp in self._temp_keys.items() if now > exp]
        for p in expired:
            Path(p).unlink(missing_ok=True)
            Path(f"{p}.pub").unlink(missing_ok=True)
            del self._temp_keys[p]
        return len(expired)

    def _start_cleanup(self):
        def loop():
            while True:
                time.sleep(300)
                self.cleanup_expired()
        threading.Thread(target=loop, daemon=True).start()
