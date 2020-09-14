package me.devnatan.katan.core.websocket

import io.ktor.http.cio.websocket.*
import me.devnatan.katan.api.io.websocket.WebSocketMessage

open class MutableWebSocketMessage(
    override val op: Int,
    override var content: Any,
    override val session: WebSocketSession
) : WebSocketMessage