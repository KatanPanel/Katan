package me.devnatan.katan.core.websocket

import me.devnatan.katan.api.io.websocket.WebSocketSession

class WebSocketServerMessage(
    op: Int,
    content: Map<String, Any>,
    session: WebSocketSession,
) : MutableWebSocketMessage(op, content, session) {

    val serverId = content.getValue("server-id") as Int

}