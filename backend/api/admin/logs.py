from fastapi import APIRouter, Depends, Query
from typing import Optional
from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession
from models import AuditLog, get_session
from api.auth import require_admin

router = APIRouter(prefix="/admin/logs", tags=["admin-logs"])


@router.get("/")
async def list_logs(page: int = Query(1, ge=1), size: int = Query(50, le=200),
                    user_id: Optional[int] = None, action: Optional[str] = None, result: Optional[str] = None,
                    db: AsyncSession = Depends(get_session), _=Depends(require_admin)):
    q = select(AuditLog).order_by(AuditLog.created_at.desc())
    if user_id: q = q.where(AuditLog.user_id == user_id)
    if action: q = q.where(AuditLog.action == action)
    if result: q = q.where(AuditLog.result == result)
    total = (await db.execute(select(func.count()).select_from(q.subquery()))).scalar()
    rows = (await db.execute(q.offset((page - 1) * size).limit(size))).scalars().all()
    return {"total": total, "page": page, "logs": [
        {"id": l.id, "user_id": l.user_id, "action": l.action, "target": l.target,
         "result": l.result, "detail": l.detail, "ip": l.ip_address, "time": str(l.created_at)} for l in rows]}
