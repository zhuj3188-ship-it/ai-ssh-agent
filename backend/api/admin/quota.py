from fastapi import APIRouter, Depends
from sqlalchemy import select, func
from sqlalchemy.ext.asyncio import AsyncSession
from models import QuotaUsage, get_session
from api.auth import require_admin

router = APIRouter(prefix="/admin/quota", tags=["admin-quota"])


@router.get("/summary")
async def summary(db: AsyncSession = Depends(get_session), _=Depends(require_admin)):
    rows = (await db.execute(select(QuotaUsage.provider, QuotaUsage.model,
        func.sum(QuotaUsage.tokens_in).label("t_in"), func.sum(QuotaUsage.tokens_out).label("t_out"),
        func.sum(QuotaUsage.cost_usd).label("cost")).group_by(QuotaUsage.provider, QuotaUsage.model))).all()
    return {"quotas": [{"provider": r.provider, "model": r.model, "tokens_in": r.t_in, "tokens_out": r.t_out, "cost_usd": round(r.cost, 4)} for r in rows]}


@router.get("/by-user/{user_id}")
async def by_user(user_id: int, db: AsyncSession = Depends(get_session), _=Depends(require_admin)):
    rows = (await db.execute(select(QuotaUsage.provider, QuotaUsage.model,
        func.sum(QuotaUsage.tokens_in), func.sum(QuotaUsage.tokens_out), func.sum(QuotaUsage.cost_usd))
        .where(QuotaUsage.user_id == user_id).group_by(QuotaUsage.provider, QuotaUsage.model))).all()
    return {"user_id": user_id, "quotas": [
        {"provider": r[0], "model": r[1], "tokens_in": r[2], "tokens_out": r[3], "cost_usd": round(r[4], 4)} for r in rows]}
