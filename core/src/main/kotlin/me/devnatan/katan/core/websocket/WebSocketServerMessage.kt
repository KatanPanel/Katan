package me.devnatan.katan.core.websocket

import io.ktor.http.cio.websocket.*
import me.devnatan.katan.api.io.websocket.WebSocketMessage

class WebSocketServerMessage(
    op: Int,
    content: Map<String, Any>,
    session: WebSocketSession
) : MutableWebSocketMessage(op, content, session) {

    val serverId = content.getValue("server_id") as Int

}