package me.devnatan.katan.webserver.websocket.message

import me.devnatan.katan.webserver.websocket.session.WebSocketSession

open class MutableWebSocketMessage(
    override val op: Int,
    override var content: Any,
    override val session: WebSocketSession
) : WebSocketMessage