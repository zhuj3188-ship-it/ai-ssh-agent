import shutil
from fastapi import APIRouter, Depends
from api.auth import require_admin

router = APIRouter(prefix="/admin/system", tags=["admin-system"])


@router.get("/health")
async def health(_=Depends(require_admin)):
    disk = shutil.disk_usage("/")
    return {"status": "ok", "disk_total_gb": round(disk.total / (1024**3), 2),
            "disk_used_gb": round(disk.used / (1024**3), 2),
            "disk_free_gb": round(disk.free / (1024**3), 2),
            "disk_percent": round(disk.used / disk.total * 100, 1)}


@router.post("/cleanup-keys")
async def cleanup_keys(_=Depends(require_admin)):
    from server import brain
    cleaned = brain.key_manager.cleanup_expired()
    return {"cleaned": cleaned}


@router.get("/providers")
async def list_providers(_=Depends(require_admin)):
    from server import brain
    return {"providers": list(brain.providers.keys()), "default": brain.default_provider}
