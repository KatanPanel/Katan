package me.devnatan.katan.webserver.websocket.session

import kotlinx.coroutines.CoroutineScope
import me.devnatan.katan.webserver.websocket.WebSocketOpCode
import me.devnatan.katan.webserver.websocket.message.WebSocketMessage
import me.devnatan.katan.webserver.websocket.message.WebSocketMessageImpl

interface WebSocketSession : CoroutineScope {

    suspend fun send(message: WebSocketMessage)

    suspend fun close()


}

suspend fun WebSocketSession.send(op: Int, data: Map<String, Any>) {
    send(WebSocketMessageImpl(op, data, this))
}