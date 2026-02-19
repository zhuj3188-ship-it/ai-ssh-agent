from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from .routes import router as api_router
from .websocket import router as ws_router
from .admin import admin_router

app = FastAPI(title="AI SSH Agent", version="4.0")

app.add_middleware(CORSMiddleware, allow_origins=["*"], allow_credentials=True,
                   allow_methods=["*"], allow_headers=["*"])

app.include_router(api_router)
app.include_router(ws_router)
app.include_router(admin_router)


@app.get("/health")
async def health():
    return {"status": "ok"}
