package me.devnatan.katan.webserver.websocket.handler

import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_CREATE
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_START
import me.devnatan.katan.webserver.websocket.message.WebSocketServerMessage

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