from .ssh_executor import SSHExecutor
from .file_manager import FileManager


class Deployer:
    def __init__(self, ssh: SSHExecutor, fm: FileManager):
        self.ssh = ssh
        self.fm = fm

    def deploy_file(self, server: str, filename: str, content: str, remote_path: str) -> dict:
        local = self.fm.generate_file(filename, content)
        ok = self.ssh.upload(server, local, remote_path)
        if not ok:
            return {"success": False, "error": "Upload failed"}
        result = self.ssh.execute(server, f"cat {remote_path} | head -5")
        return {"success": True, "remote_path": remote_path, "preview": result.stdout[:500]}

    def deploy_project(self, server: str, project_name: str,
                       files: dict[str, str], remote_dir: str,
                       setup_command: str = "") -> dict:
        local_dir = self.fm.generate_project(project_name, files)
        archive = self.fm.compress(local_dir, "tar.gz")
        remote_archive = f"/tmp/{project_name}.tar.gz"
        ok = self.ssh.upload(server, archive, remote_archive)
        if not ok:
            return {"success": False, "error": "Upload failed"}
        self.ssh.execute(server, f"mkdir -p {remote_dir}")
        extract = self.ssh.execute(server, f"tar -xzf {remote_archive} -C {remote_dir} && rm {remote_archive}")
        if not extract.success:
            return {"success": False, "error": extract.stderr}
        result = {"success": True, "remote_dir": remote_dir, "files": list(files.keys())}
        if setup_command:
            setup = self.ssh.execute(server, f"cd {remote_dir}/{project_name} && {setup_command}")
            result["setup_output"] = setup.stdout[:2000]
            result["setup_success"] = setup.success
        return result
