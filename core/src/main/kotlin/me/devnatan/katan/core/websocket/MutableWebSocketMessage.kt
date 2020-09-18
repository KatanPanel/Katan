package me.devnatan.katan.core.websocket

import me.devnatan.katan.api.io.websocket.WebSocketMessage
import me.devnatan.katan.api.io.websocket.WebSocketSession

open class MutableWebSocketMessage(
    override val op: Int,
    override var content: Any,
    override val session: WebSocketSession,
) : WebSocketMessage