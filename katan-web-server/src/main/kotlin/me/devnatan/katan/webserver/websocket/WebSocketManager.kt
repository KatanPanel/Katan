package me.devnatan.katan.webserver.websocket

import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.cio.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import me.devnatan.katan.api.Katan
import me.devnatan.katan.api.logging.logger
import me.devnatan.katan.webserver.KatanWS
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.DATA_KEY
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.INVALID
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.OP_KEY
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_EXEC
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_LOGS
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_START
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_STATS
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_STATS_CANCEL_STREAMING
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_STATS_START_STREAMING
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_STOP
import me.devnatan.katan.webserver.websocket.handlers.*
import me.devnatan.katan.webserver.websocket.message.WebSocketMessage
import me.devnatan.katan.webserver.websocket.message.WebSocketMessageImpl
import me.devnatan.katan.webserver.websocket.session.KtorWebSocketSession
import me.devnatan.katan.webserver.websocket.session.WebSocketSession
import me.devnatan.katan.webserver.websocket.session.send
import org.slf4j.Logger
import java.nio.channels.ClosedChannelException
import java.util.*

class WebSocketManager(val katan: Katan) {

    companion object {
        val log: Logger = logger<WebSocketManager>()
    }

    private val sessions =
        Collections.synchronizedSet<WebSocketSession>(LinkedHashSet())

    fun listen() {
        /* scope.launch(Dispatchers.IO + CoroutineName("Propagator")) {
            mainEventScope.listen<ServerStateChangeEvent>().collect { event ->
                println(
                    "[WS Propagator] (${sessions.size}) ${
                        event
                            .server.id
                    }"
                )

                for (session in sessions) {
                    session.send(
                        SERVER_UPDATED,
                        mapOf(
                            "old-state" to event.oldState,
                            "new-state" to event.newState,
                            "server-id" to event.server.id
                        ),
                    )
                }
            }
        } */
    }

    suspend fun handleMessage(message: WebSocketMessage) {
        runCatching {
            println("[WS Message Listener] ~> $message")
            when (message.op) {
                SERVER_START -> wsServerStart(message)
                SERVER_STOP -> wsServerStop(message)
                SERVER_STATS -> wsServerStats(message)
                SERVER_STATS_START_STREAMING -> wsServerStatsStreamingStart(
                    message
                )
                SERVER_STATS_CANCEL_STREAMING -> wsServerStatsStreamingCancel(
                    message
                )
                SERVER_LOGS -> wsServerLogs(message)
                SERVER_EXEC -> wsServerExec(message)
                else -> message.session.send(
                    INVALID,
                    mapOf("code" to message.op)
                )
            }
        }.onFailure { error ->
            log.error(
                "[WS Message Listener] Failed to invoke ${message.op}: $error"
            )
            log.trace(null, error)
        }
    }

    suspend inline fun handleSession(session: DefaultWebSocketServerSession) {
        val impl = KtorWebSocketSession(session)

        attachSession(impl)
        log.info("[Session] Attached.")

        try {
            log.info("[Session] Waiting for new messages...")
            for (frame in session.incoming) {
                if (frame !is Frame.Text)
                    continue

                val data = KatanWS.objectMapper.readValue<Map<String, Any>>(
                    frame.readText()
                )

                log.info("[Session] Resolving text frame.")
                log.info("[Session] Frame: $data")
                val op = data[OP_KEY] as? Int?
                log.info("[Session] Frame op code: $op")

                if (op == null) {
                    log.error("Operation code not provided")
                    return
                }

                @Suppress("UNCHECKED_CAST")
                val content = data[DATA_KEY] as? Map<String, Any>?

                log.info("[Session] Frame content: $content")
                handleMessage(
                    WebSocketMessageImpl(
                        op,
                        content,
                        impl
                    )
                )
            }
            log.info("[Session] for completed")
        } catch (e: ClosedReceiveChannelException) {
            log.info("[Session] receive channel closed")
            session.closeReason.await()
        } catch (e: Throwable) {
            log.info("[Session] error: $e")
            session.closeReason.await()
            e.printStackTrace()
        } finally {
            detachSession(impl)
            log.info("[Session] detached")
        }
    }

    /**
     * Closes and removes all active [WebSocketSession],
     */
    suspend fun close() {
        val iterator = sessions.iterator()
        while (iterator.hasNext()) {
            detachSession(iterator.next(), remove = false)
            iterator.remove()
        }
    }

    /**
     * Attach the new incoming [WebSocketSession] to the connected clients list.
     */
    fun attachSession(session: WebSocketSession) {
        sessions += session
    }

    /**
     * Closes a [WebSocketSession] and detaches it from the list of connected clients.
     */
    suspend fun detachSession(
        session: WebSocketSession,
        remove: Boolean = true
    ) {
        try {
            session.close()
            println("[WS] Session $session closed.")
            return
        } catch (_: ClosedChannelException) {
        }

        if (remove)
            sessions -= session
    }

}