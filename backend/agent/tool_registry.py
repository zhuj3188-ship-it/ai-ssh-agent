TOOLS = [
    {"name": "ssh_execute", "description": "Execute command on remote server via SSH",
     "input_schema": {"type": "object", "properties": {
         "server": {"type": "string"}, "command": {"type": "string"}}, "required": ["server", "command"]}},
    {"name": "list_servers", "description": "List all configured SSH servers",
     "input_schema": {"type": "object", "properties": {}}},
    {"name": "test_connection", "description": "Test SSH connectivity",
     "input_schema": {"type": "object", "properties": {"server": {"type": "string"}}, "required": ["server"]}},
    {"name": "generate_file", "description": "Generate a file in workspace",
     "input_schema": {"type": "object", "properties": {
         "filename": {"type": "string"}, "content": {"type": "string"},
         "subdir": {"type": "string"}}, "required": ["filename", "content"]}},
    {"name": "generate_project", "description": "Generate multi-file project",
     "input_schema": {"type": "object", "properties": {
         "project_name": {"type": "string"}, "files": {"type": "object"}}, "required": ["project_name", "files"]}},
    {"name": "compress_file", "description": "Compress file or directory",
     "input_schema": {"type": "object", "properties": {
         "source": {"type": "string"}, "format": {"type": "string"}}, "required": ["source"]}},
    {"name": "extract_file", "description": "Extract archive",
     "input_schema": {"type": "object", "properties": {
         "archive": {"type": "string"}, "dest": {"type": "string"}}, "required": ["archive"]}},
    {"name": "list_workspace", "description": "List workspace files",
     "input_schema": {"type": "object", "properties": {"subdir": {"type": "string"}}}},
    {"name": "file_upload", "description": "Upload file to server via SFTP",
     "input_schema": {"type": "object", "properties": {
         "server": {"type": "string"}, "local_path": {"type": "string"},
         "remote_path": {"type": "string"}}, "required": ["server", "local_path", "remote_path"]}},
    {"name": "file_download", "description": "Download file from server",
     "input_schema": {"type": "object", "properties": {
         "server": {"type": "string"}, "remote_path": {"type": "string"},
         "local_path": {"type": "string"}}, "required": ["server", "remote_path", "local_path"]}},
    {"name": "deploy_file", "description": "Generate and deploy file to server",
     "input_schema": {"type": "object", "properties": {
         "server": {"type": "string"}, "filename": {"type": "string"},
         "content": {"type": "string"}, "remote_path": {"type": "string"}},
         "required": ["server", "filename", "content", "remote_path"]}},
    {"name": "deploy_project", "description": "Generate and deploy project to server",
     "input_schema": {"type": "object", "properties": {
         "server": {"type": "string"}, "project_name": {"type": "string"},
         "files": {"type": "object"}, "remote_dir": {"type": "string"},
         "setup_command": {"type": "string"}},
         "required": ["server", "project_name", "files", "remote_dir"]}},
]
