from datetime import datetime, timezone
from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from typing import Optional
from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from models import User, Device, AuditLog, QuotaUsage, ChatHistory, get_session
from api.auth import get_current_user, hash_password, verify_password, create_token, require_admin

router = APIRouter(prefix="/api", tags=["api"])


class LoginRequest(BaseModel):
    username: str
    password: str

class RegisterRequest(BaseModel):
    username: str
    email: str
    password: str

class ChatRequest(BaseModel):
    message: str
    provider: Optional[str] = None
    session_id: Optional[str] = "default"


@router.post("/auth/login")
async def login(body: LoginRequest, db: AsyncSession = Depends(get_session)):
    user = (await db.execute(select(User).where(User.username == body.username))).scalar_one_or_none()
    if not user or not verify_password(body.password, user.hashed_password):
        raise HTTPException(401, "Invalid credentials")
    if user.is_banned: raise HTTPException(403, "Account banned")
    user.last_login = datetime.now()
    await db.commit()
    token = create_token(user.id, user.username, user.role)
    return {"token": token, "user": {"id": user.id, "username": user.username, "role": user.role}}


@router.post("/auth/register")
async def register(body: RegisterRequest, db: AsyncSession = Depends(get_session)):
    exists = (await db.execute(select(User).where(User.username == body.username))).scalar_one_or_none()
    if exists: raise HTTPException(409, "Username taken")
    user = User(username=body.username, email=body.email, hashed_password=hash_password(body.password))
    db.add(user)
    await db.commit()
    await db.refresh(user)
    token = create_token(user.id, user.username, user.role)
    return {"token": token, "user": {"id": user.id, "username": user.username}}


@router.post("/chat")
async def chat(body: ChatRequest, user: User = Depends(get_current_user), db: AsyncSession = Depends(get_session)):
    from server import brain
    result = await brain.chat(body.message, provider_name=body.provider, user_id=user.id, session_id=body.session_id)
    db.add(ChatHistory(user_id=user.id, session_id=body.session_id, role="user", content=body.message))
    db.add(ChatHistory(user_id=user.id, session_id=body.session_id, role="assistant",
        content=result["reply"], provider=result.get("provider"), model=result.get("model"),
        tokens_used=result.get("tokens_in", 0) + result.get("tokens_out", 0)))
    if result.get("tokens_in"):
        db.add(QuotaUsage(user_id=user.id, provider=result.get("provider", ""),
            model=result.get("model", ""), tokens_in=result.get("tokens_in", 0),
            tokens_out=result.get("tokens_out", 0), cost_usd=result.get("cost", 0)))
    db.add(AuditLog(user_id=user.id, action="ai_chat", target=body.message[:200], result="success", ip_address=""))
    await db.commit()
    return result


@router.get("/servers")
async def list_servers(user: User = Depends(get_current_user)):
    from server import brain
    return {"servers": brain.ssh.list_servers()}


@router.post("/servers/{name}/test")
async def test_server(name: str, user: User = Depends(get_current_user)):
    from server import brain
    return brain.ssh.test_connection(name)


@router.get("/quota")
async def my_quota(user: User = Depends(get_current_user), db: AsyncSession = Depends(get_session)):
    from sqlalchemy import func
    rows = (await db.execute(select(QuotaUsage.provider, QuotaUsage.model,
        func.sum(QuotaUsage.tokens_in), func.sum(QuotaUsage.tokens_out),
        func.sum(QuotaUsage.cost_usd)).where(QuotaUsage.user_id == user.id)
        .group_by(QuotaUsage.provider, QuotaUsage.model))).all()
    return {"quotas": [{"provider": r[0], "model": r[1], "tokens_in": r[2], "tokens_out": r[3], "cost_usd": round(r[4], 4)} for r in rows]}


@router.get("/profile")
async def profile(user: User = Depends(get_current_user)):
    return {"id": user.id, "username": user.username, "email": user.email,
            "role": user.role, "language": user.language, "preferred_provider": user.preferred_provider}
