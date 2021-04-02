package me.devnatan.katan.webserver.websocket.session

import kotlinx.coroutines.CoroutineScope
import me.devnatan.katan.webserver.websocket.message.WebSocketMessage

interface WebSocketSession : CoroutineScope {

    suspend fun send(message: WebSocketMessage)

    suspend fun close()


}