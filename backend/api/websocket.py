import json
import logging
from fastapi import APIRouter, WebSocket, WebSocketDisconnect
from api.auth import decode_token

logger = logging.getLogger("websocket")
router = APIRouter()


@router.websocket("/ws/chat")
async def ws_chat(ws: WebSocket):
    await ws.accept()
    try:
        auth_msg = await ws.receive_text()
        data = json.loads(auth_msg)
        payload = decode_token(data.get("token", ""))
        user_id = payload["user_id"]
    except Exception:
        await ws.close(code=4001, reason="Auth failed")
        return
    from server import brain
    session_id = "ws_default"
    try:
        while True:
            raw = await ws.receive_text()
            msg = json.loads(raw)
            session_id = msg.get("session_id", session_id)
            await ws.send_text(json.dumps({"type": "thinking", "content": "Processing..."}))
            result = await brain.chat(
                msg.get("message", ""),
                provider_name=msg.get("provider"),
                user_id=user_id,
                session_id=session_id
            )
            await ws.send_text(json.dumps({
                "type": "reply",
                "content": result["reply"],
                "tokens_in": result.get("tokens_in", 0),
                "tokens_out": result.get("tokens_out", 0),
                "cost": result.get("cost", 0),
                "provider": result.get("provider", ""),
                "model": result.get("model", "")
            }))
    except WebSocketDisconnect:
        logger.info(f"User {user_id} disconnected")
    except Exception:
        logger.exception("WebSocket error")
        await ws.close(code=1011)
