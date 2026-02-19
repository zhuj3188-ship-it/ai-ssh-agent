import os
import time
import shutil
import psutil
from fastapi import APIRouter, Depends
from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncSession
from models.base import get_session
from api.auth import require_admin

router = APIRouter(prefix="/admin/system", tags=["admin-system"])

START_TIME = time.time()


@router.get("/health")
async def health(db: AsyncSession = Depends(get_session), _=Depends(require_admin)):
    # Disk
    disk = shutil.disk_usage("/")

    # Memory
    mem = psutil.virtual_memory()

    # CPU
    cpu = psutil.cpu_percent(interval=0.5)

    # Uptime
    uptime_sec = int(time.time() - START_TIME)
    days = uptime_sec // 86400
    hours = (uptime_sec % 86400) // 3600
    mins = (uptime_sec % 3600) // 60
    if days > 0:
        uptime_str = f"{days}d {hours}h {mins}m"
    elif hours > 0:
        uptime_str = f"{hours}h {mins}m"
    else:
        uptime_str = f"{mins}m"

    # DB check
    db_ok = False
    try:
        await db.execute(text("SELECT 1"))
        db_ok = True
    except Exception:
        pass

    return {
        "status": "ok",
        "disk_total_gb": round(disk.total / (1024**3), 2),
        "disk_used_gb": round(disk.used / (1024**3), 2),
        "disk_free_gb": round(disk.free / (1024**3), 2),
        "disk_percent": round(disk.used / disk.total * 100, 1),
        "mem_total_gb": round(mem.total / (1024**3), 2),
        "mem_used_gb": round(mem.used / (1024**3), 2),
        "mem_percent": mem.percent,
        "cpu_percent": cpu,
        "uptime": uptime_str,
        "db_ok": db_ok,
    }


@router.post("/cleanup-keys")
async def cleanup_keys(_=Depends(require_admin)):
    from server import brain
    cleaned = brain.key_manager.cleanup_expired()
    return {"cleaned": cleaned}


@router.get("/providers")
async def list_providers(_=Depends(require_admin)):
    from server import brain
    return {"providers": list(brain.providers.keys()), "default": brain.default_provider}
