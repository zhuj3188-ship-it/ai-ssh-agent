import os
import yaml
import logging
from contextlib import asynccontextmanager
import uvicorn
from models import init_db
from api.app import app
from agent import AIBrain

logging.basicConfig(level=logging.INFO, format="%(asctime)s %(name)s %(levelname)s %(message)s")
logger = logging.getLogger("server")


def load_config() -> dict:
    config_path = os.getenv("CONFIG_PATH", "config.yaml")
    with open(config_path) as f:
        raw = f.read()
    for key, val in os.environ.items():
        raw = raw.replace(f"${{{key}}}", val)
    return yaml.safe_load(raw)


config = load_config()
brain = AIBrain(config)


@asynccontextmanager
async def lifespan(application):
    logger.info("Initializing database...")
    await init_db()
    logger.info("AI SSH Agent v5.0 started")
    yield
    brain.ssh.close_all()
    logger.info("Shutdown complete")


app.router.lifespan_context = lifespan

if __name__ == "__main__":
    uvicorn.run("server:app", host="0.0.0.0", port=8000, reload=False)
