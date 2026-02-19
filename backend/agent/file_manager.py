import os
import shutil
import tarfile
import zipfile
from pathlib import Path


class FileManager:
    def __init__(self, workspace: str = "/var/workspace"):
        self.workspace = Path(workspace)
        self.workspace.mkdir(parents=True, exist_ok=True)

    def generate_file(self, filename: str, content: str, subdir: str = "") -> str:
        target_dir = self.workspace / subdir if subdir else self.workspace
        target_dir.mkdir(parents=True, exist_ok=True)
        path = target_dir / filename
        path.write_text(content, encoding="utf-8")
        return str(path)

    def generate_project(self, project_name: str, files: dict[str, str]) -> str:
        project_dir = self.workspace / project_name
        for rel_path, content in files.items():
            full = project_dir / rel_path
            full.parent.mkdir(parents=True, exist_ok=True)
            full.write_text(content, encoding="utf-8")
        return str(project_dir)

    def compress(self, source: str, fmt: str = "tar.gz") -> str:
        src = Path(source)
        if fmt == "zip":
            out = str(src) + ".zip"
            with zipfile.ZipFile(out, "w", zipfile.ZIP_DEFLATED) as zf:
                if src.is_dir():
                    for f in src.rglob("*"):
                        if f.is_file():
                            zf.write(f, f.relative_to(src.parent))
                else:
                    zf.write(src, src.name)
        else:
            out = str(src) + ".tar.gz"
            with tarfile.open(out, "w:gz") as tf:
                tf.add(str(src), arcname=src.name)
        return out

    def extract(self, archive: str, dest: str = "") -> str:
        arc = Path(archive)
        target = Path(dest) if dest else arc.parent / arc.stem.replace(".tar", "")
        target.mkdir(parents=True, exist_ok=True)
        if arc.suffix == ".zip":
            with zipfile.ZipFile(str(arc), "r") as zf:
                zf.extractall(str(target))
        else:
            with tarfile.open(str(arc), "r:*") as tf:
                tf.extractall(str(target))
        return str(target)

    def list_workspace(self, subdir: str = "") -> list[dict]:
        target = self.workspace / subdir if subdir else self.workspace
        items = []
        if not target.exists():
            return items
        for f in sorted(target.iterdir()):
            items.append({"name": f.name, "type": "dir" if f.is_dir() else "file",
                          "size": f.stat().st_size if f.is_file() else 0})
        return items
