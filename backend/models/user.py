from sqlalchemy import Column, Integer, String, Boolean, DateTime
from sqlalchemy.sql import func
from .base import Base


class User(Base):
    __tablename__ = "users"

    id = Column(Integer, primary_key=True, autoincrement=True)
    username = Column(String(50), unique=True, nullable=False, index=True)
    email = Column(String(120), unique=True, nullable=True)
    hashed_password = Column(String(256), nullable=False)
    role = Column(String(20), default="user")
    is_active = Column(Boolean, default=True)
    is_banned = Column(Boolean, default=False)
    max_sessions = Column(Integer, default=3)
    preferred_provider = Column(String(30), default="anthropic")
    language = Column(String(10), default="zh")
    created_at = Column(DateTime, server_default=func.now())
    last_login = Column(DateTime, nullable=True)
