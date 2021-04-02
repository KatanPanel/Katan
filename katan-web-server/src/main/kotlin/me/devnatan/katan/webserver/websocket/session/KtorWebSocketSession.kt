package me.devnatan.katan.webserver.websocket.session

import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.Job
import me.devnatan.katan.webserver.websocket.message.WebSocketMessage
import io.ktor.http.cio.websocket.WebSocketSession as KtorSession

class KtorWebSocketSession(
    delegate: KtorSession,
    @JvmField private inline val writer: suspend (WebSocketMessage) -> Unit
) : KtorSession by delegate, WebSocketSession {

    val serverStatsStreaming: MutableMap<Int, Job> by lazy { hashMapOf() }

    init {
        coroutineContext[Job]!!.invokeOnCompletion { error ->
            error?.printStackTrace()
        }
    }

    override suspend fun send(message: WebSocketMessage) {
        writer(message)
    }

    override suspend fun close() {
        (this as KtorSession).close()
    }

}