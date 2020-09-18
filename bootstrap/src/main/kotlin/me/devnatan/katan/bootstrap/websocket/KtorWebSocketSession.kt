package me.devnatan.katan.bootstrap.websocket

import kotlinx.coroutines.cancel
import me.devnatan.katan.api.io.websocket.WebSocketSession
import io.ktor.http.cio.websocket.WebSocketSession as KtorSession

abstract class KtorWebSocketSession(
    delegate: KtorSession,
) : KtorSession by delegate, WebSocketSession {

    override fun close() {
        (this as KtorSession).cancel()
    }

}