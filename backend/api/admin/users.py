from fastapi import APIRouter, Depends, HTTPException, Query
from pydantic import BaseModel
from typing import Optional
from sqlalchemy import select, update, func
from sqlalchemy.ext.asyncio import AsyncSession
from models import User, Device, get_session
from api.auth import require_admin, hash_password

router = APIRouter(prefix="/admin/users", tags=["admin-users"])


class UserCreate(BaseModel):
    username: str
    email: str
    password: str
    role: str = "user"

class UserUpdate(BaseModel):
    is_active: Optional[bool] = None
    is_banned: Optional[bool] = None
    role: Optional[str] = None
    max_sessions: Optional[int] = None


@router.get("/")
async def list_users(page: int = Query(1, ge=1), size: int = Query(20, le=100), search: str = "",
                     db: AsyncSession = Depends(get_session), _=Depends(require_admin)):
    q = select(User)
    if search: q = q.where(User.username.contains(search) | User.email.contains(search))
    total = (await db.execute(select(func.count()).select_from(q.subquery()))).scalar()
    rows = (await db.execute(q.offset((page - 1) * size).limit(size))).scalars().all()
    return {"total": total, "page": page, "size": size, "users": [
        {"id": u.id, "username": u.username, "email": u.email, "role": u.role,
         "is_active": u.is_active, "is_banned": u.is_banned, "max_sessions": u.max_sessions,
         "last_login": str(u.last_login) if u.last_login else None, "created_at": str(u.created_at)} for u in rows]}


@router.post("/")
async def create_user(body: UserCreate, db: AsyncSession = Depends(get_session), _=Depends(require_admin)):
    user = User(username=body.username, email=body.email, hashed_password=hash_password(body.password), role=body.role)
    db.add(user)
    await db.commit()
    await db.refresh(user)
    return {"id": user.id, "username": user.username}


@router.patch("/{user_id}")
async def update_user(user_id: int, body: UserUpdate, db: AsyncSession = Depends(get_session), _=Depends(require_admin)):
    vals = body.dict(exclude_none=True)
    if not vals: raise HTTPException(400, "Nothing to update")
    await db.execute(update(User).where(User.id == user_id).values(**vals))
    await db.commit()
    return {"ok": True}


@router.delete("/{user_id}")
async def delete_user(user_id: int, db: AsyncSession = Depends(get_session), _=Depends(require_admin)):
    user = (await db.execute(select(User).where(User.id == user_id))).scalar_one_or_none()
    if not user: raise HTTPException(404)
    await db.delete(user)
    await db.commit()
    return {"ok": True}


@router.get("/{user_id}/devices")
async def user_devices(user_id: int, db: AsyncSession = Depends(get_session), _=Depends(require_admin)):
    rows = (await db.execute(select(Device).where(Device.user_id == user_id))).scalars().all()
    return {"devices": [{"id": d.id, "device_name": d.device_name, "device_type": d.device_type,
        "app_version": d.app_version, "is_online": d.is_online,
        "last_active": str(d.last_active), "ip_address": d.ip_address} for d in rows]}
