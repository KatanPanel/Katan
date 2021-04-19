package me.devnatan.katan.webserver.websocket.session

import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import me.devnatan.katan.webserver.KatanWS
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.DATA_KEY
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.OP_KEY
import me.devnatan.katan.webserver.websocket.message.WebSocketMessage

class KtorWebSocketSession(
    delegate: WebSocketServerSession,
) : WebSocketServerSession by delegate, WebSocketSession {

    private var serverStatsStreaming: Job? = null

    suspend fun startServerStatsStreaming(job: Job) {
        serverStatsStreaming?.cancelAndJoin()
        serverStatsStreaming = job.also { it.start() }
    }

    suspend fun cancelServerStatsStreaming(): Boolean {
        if (serverStatsStreaming == null)
            return false

        if (serverStatsStreaming!!.isActive)
            serverStatsStreaming?.cancelAndJoin()

        serverStatsStreaming = null
        return true
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun send(message: WebSocketMessage) {
        outgoing.send(
            Frame.Text(
                KatanWS.objectMapper.writeValueAsString(
                    mapOf(
                        OP_KEY to message.op,
                        DATA_KEY to message.content
                    )
                )
            )
        )
    }

    override suspend fun close() {
        (this as WebSocketServerSession).close()
    }

}