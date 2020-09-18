package me.devnatan.katan.core.websocket

import me.devnatan.katan.api.io.websocket.WebSocketHandler
import me.devnatan.katan.api.io.websocket.WebSocketOpCode.SERVER_CREATE
import me.devnatan.katan.api.io.websocket.WebSocketOpCode.SERVER_START

class WebSocketServerHandler : WebSocketHandler<WebSocketServerMessage, Unit> {

    override val mappings = mapOf(
        SERVER_CREATE to this::onServerCreate,
        SERVER_START to this::onServerStart
    )

    private fun onServerCreate(message: WebSocketServerMessage) {
    }

    private fun onServerStart(message: WebSocketServerMessage) {

    }

}