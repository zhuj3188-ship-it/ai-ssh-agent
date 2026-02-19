package com.aissh.agent.data.remote

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.*
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WebSocketManager @Inject constructor(private val client: OkHttpClient) {
    private var ws: WebSocket? = null
    private val _state = MutableStateFlow("disconnected")
    val connectionState = _state.asStateFlow()
    val incoming = Channel<String>(Channel.BUFFERED)

    fun connect(serverUrl: String, token: String) {
        val wsUrl = serverUrl.replace("http", "ws") + "ws/chat"
        ws = client.newWebSocket(Request.Builder().url(wsUrl).build(), object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                _state.value = "connected"
                webSocket.send(JSONObject().put("token", token).toString())
            }
            override fun onMessage(webSocket: WebSocket, text: String) { incoming.trySend(text) }
            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) { _state.value = "disconnected" }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) { _state.value = "disconnected" }
        })
    }

    fun send(message: String, provider: String? = null) {
        ws?.send(JSONObject().apply { put("message", message); provider?.let { put("provider", it) } }.toString())
    }

    fun disconnect() { ws?.close(1000, "bye"); ws = null; _state.value = "disconnected" }
}
