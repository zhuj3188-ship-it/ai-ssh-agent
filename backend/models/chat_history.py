from sqlalchemy import Column, Integer, String, Text, DateTime, ForeignKey
from sqlalchemy.sql import func
from .base import Base


class ChatHistory(Base):
    __tablename__ = "chat_history"

    id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), index=True)
    session_id = Column(String(64), index=True)
    role = Column(String(20))
    content = Column(Text)
    provider = Column(String(30), nullable=True)
    model = Column(String(60), nullable=True)
    tokens_used = Column(Integer, default=0)
    created_at = Column(DateTime, server_default=func.now())
