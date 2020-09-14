package me.devnatan.katan.core.websocket

import me.devnatan.katan.api.io.websocket.WebSocketHandler
import me.devnatan.katan.api.io.websocket.WebSocketHandlerMapper
import me.devnatan.katan.api.io.websocket.WebSocketMessage
import me.devnatan.katan.api.io.websocket.WebSocketOpCode.SERVER_CREATE
import me.devnatan.katan.api.io.websocket.WebSocketOpCode.SERVER_START

class WebSocketServerHandler : WebSocketHandler<WebSocketServerMessage, Any> {

    override fun next(message: WebSocketServerMessage): Any {
        return message
    }

    override fun mappings(): Map<Int, WebSocketHandlerMapper<WebSocketServerMessage>> {
        return mapOf(
            SERVER_CREATE to this::onServerCreate,
            SERVER_START to this::onServerStart
        )
    }

    private fun onServerCreate(message: WebSocketServerMessage) {
    }

    private fun onServerStart(message: WebSocketServerMessage) {

    }

}