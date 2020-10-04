package me.devnatan.katan.webserver.websocket.handler

import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_CREATE
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_START
import me.devnatan.katan.webserver.websocket.message.WebSocketMessage

object WebSocketServerHandler : WebSocketHandler() {

    init {
        handle(SERVER_CREATE) {
            throw NotImplementedError()
        }

        handle(SERVER_START) {
            throw NotImplementedError()
        }
    }

}

val WebSocketMessage.serverId
    get() = content.getValue("server-id") as String