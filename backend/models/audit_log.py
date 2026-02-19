from sqlalchemy import Column, Integer, String, DateTime, ForeignKey
from sqlalchemy.sql import func
from .base import Base


class AuditLog(Base):
    __tablename__ = "audit_logs"

    id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="SET NULL"), index=True, nullable=True)
    action = Column(String(50), index=True)
    target = Column(String(500))
    result = Column(String(20))
    detail = Column(String(4000), nullable=True)
    ip_address = Column(String(45))
    created_at = Column(DateTime, server_default=func.now())
