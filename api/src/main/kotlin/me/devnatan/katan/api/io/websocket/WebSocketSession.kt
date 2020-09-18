package me.devnatan.katan.api.io.websocket

import java.io.Closeable

interface WebSocketSession : Closeable {

    suspend fun send(message: WebSocketMessage)

}