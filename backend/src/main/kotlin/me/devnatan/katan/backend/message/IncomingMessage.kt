package me.devnatan.katan.backend.message

import io.ktor.http.cio.websocket.WebSocketSession

class IncomingMessage(delegate: Message, val session: WebSocketSession): Message by delegate {

    fun isCommand(command: String): Boolean {
        return (this.contains("command")) && (this["command"] as? String).equals(command)
    }

}