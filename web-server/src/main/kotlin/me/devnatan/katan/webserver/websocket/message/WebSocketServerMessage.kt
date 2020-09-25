package me.devnatan.katan.webserver.websocket.message

import me.devnatan.katan.webserver.websocket.session.WebSocketSession

class WebSocketServerMessage(
    op: Int,
    content: Map<String, Any>,
    session: WebSocketSession,
) : MutableWebSocketMessage(op, content, session) {

    val serverId = content.getValue("server-id")

}