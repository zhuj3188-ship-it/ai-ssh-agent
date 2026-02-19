from datetime import datetime, timedelta
from fastapi import APIRouter, Depends
from sqlalchemy import func, select
from sqlalchemy.ext.asyncio import AsyncSession
from models import User, Device, AuditLog, QuotaUsage, get_session
from api.auth import require_admin

router = APIRouter(prefix="/admin", tags=["admin"])


@router.get("/dashboard")
async def dashboard(db: AsyncSession = Depends(get_session), _=Depends(require_admin)):
    now = datetime.now()
    day_ago = now - timedelta(days=1)
    week_ago = now - timedelta(days=7)
    total_users = (await db.execute(select(func.count(User.id)))).scalar() or 0
    active_today = (await db.execute(select(func.count(User.id)).where(User.last_login >= day_ago))).scalar() or 0
    online_devices = (await db.execute(select(func.count(Device.id)).where(Device.is_online == True))).scalar() or 0
    cmds_today = (await db.execute(select(func.count(AuditLog.id)).where(AuditLog.action == "ssh_execute", AuditLog.created_at >= day_ago))).scalar() or 0
    blocked_today = (await db.execute(select(func.count(AuditLog.id)).where(AuditLog.result == "blocked", AuditLog.created_at >= day_ago))).scalar() or 0
    cost_week = (await db.execute(select(func.sum(QuotaUsage.cost_usd)).where(QuotaUsage.created_at >= week_ago))).scalar() or 0.0
    daily = []
    for i in range(7):
        d_start = (now - timedelta(days=6 - i)).replace(hour=0, minute=0, second=0, microsecond=0)
        d_end = d_start + timedelta(days=1)
        cnt = (await db.execute(select(func.count(AuditLog.id)).where(AuditLog.created_at >= d_start, AuditLog.created_at < d_end))).scalar() or 0
        daily.append({"date": d_start.strftime("%m-%d"), "commands": cnt})
    return {"total_users": total_users, "active_today": active_today, "online_devices": online_devices,
            "commands_today": cmds_today, "blocked_today": blocked_today,
            "cost_this_week_usd": round(cost_week, 4), "daily_trend": daily}
