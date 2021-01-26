package me.devnatan.katan.webserver.websocket.session

import io.ktor.http.cio.websocket.*
import me.devnatan.katan.webserver.websocket.message.WebSocketMessage
import io.ktor.http.cio.websocket.WebSocketSession as KtorSession

open class KtorWebSocketSession(
    delegate: KtorSession,
    @JvmField private inline val writer: suspend (WebSocketMessage) -> Unit
) : KtorSession by delegate, WebSocketSession {

    override suspend fun send(message: WebSocketMessage) {
        writer(message)
    }

    override suspend fun close() {
        (this as KtorSession).close()
    }

}