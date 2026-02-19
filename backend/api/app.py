from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from .routes import router as api_router
from .websocket import router as ws_router
from .admin import admin_router
from .provider_routes import router as provider_router

app = FastAPI(title="AI SSH Agent", version="5.0")

app.add_middleware(CORSMiddleware,
    allow_origins=["http://43.139.24.49:8080", "http://localhost:8080"],
    allow_credentials=True, allow_methods=["*"], allow_headers=["*"])

app.include_router(api_router)
app.include_router(ws_router)
app.include_router(admin_router)
app.include_router(provider_router)


@app.get("/health")
async def health():
    return {"status": "ok"}
