from fastapi import APIRouter
from . import dashboard, users, logs, quota, system

admin_router = APIRouter(prefix="/api")
admin_router.include_router(dashboard.router)
admin_router.include_router(users.router)
admin_router.include_router(logs.router)
admin_router.include_router(quota.router)
admin_router.include_router(system.router)
