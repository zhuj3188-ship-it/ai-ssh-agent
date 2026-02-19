from sqlalchemy import Column, Integer, String, Boolean, DateTime, ForeignKey
from sqlalchemy.sql import func
from .base import Base


class Device(Base):
    __tablename__ = "devices"

    id = Column(Integer, primary_key=True, autoincrement=True)
    user_id = Column(Integer, ForeignKey("users.id", ondelete="CASCADE"), index=True)
    device_name = Column(String(100))
    device_type = Column(String(20))
    app_version = Column(String(20))
    os_version = Column(String(50))
    push_token = Column(String(256), nullable=True)
    ip_address = Column(String(45))
    is_online = Column(Boolean, default=False)
    last_active = Column(DateTime, server_default=func.now())
    created_at = Column(DateTime, server_default=func.now())
