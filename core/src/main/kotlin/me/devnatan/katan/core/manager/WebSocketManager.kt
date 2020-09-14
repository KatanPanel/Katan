package me.devnatan.katan.core.manager

import br.com.devsrsouza.eventkt.listen
import br.com.devsrsouza.eventkt.scopes.LocalEventScope
import br.com.devsrsouza.eventkt.scopes.asSimple
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.transform
import me.devnatan.katan.api.io.websocket.WebSocketHandler
import me.devnatan.katan.api.io.websocket.WebSocketMessage
import me.devnatan.katan.core.Katan
import me.devnatan.katan.core.websocket.MutableWebSocketMessage
import java.util.concurrent.CopyOnWriteArrayList

class WebSocketManager(private val core: Katan) {

    private val sessions = CopyOnWriteArrayList<WebSocketSession>()
    private val handlers = mutableListOf<WebSocketHandler<WebSocketMessage, *>>()
    private val eventbus = LocalEventScope().asSimple()
    private val scope = CoroutineScope(Dispatchers.IO + CoroutineName(WebSocketManager::class.simpleName!!))

    companion object {
        val ABNORMAL_CLOSE = CloseReason(CloseReason.Codes.PROTOCOL_ERROR, "Protocol Error")
    }

    init {
        eventbus.listen<WebSocketMessage>().transform { message ->
            for (handler in handlers) {
                val mappings = handler.mappings()
                if (!mappings.containsKey(message.op))
                    continue

                if (message is MutableWebSocketMessage) {
                    val result = handler.next(message) ?: continue
                    if (result is WebSocketHandler.NOTHING)
                        continue

                    message.content = result
                }

                mappings.getValue(message.op)(message)
                emit(message.content)
            }
        }.launchIn(scope)
    }

    fun registerEventHandler(vararg handlers: WebSocketHandler<WebSocketMessage, *>) {
        synchronized (this.handlers) {
            this.handlers.addAll(handlers)
        }
    }

    fun unregisterEventHandler(handler: WebSocketHandler<WebSocketMessage, *>) {
        synchronized (handlers) {
            handlers.remove(handler)
        }
    }

    /**
     * Attach the new incoming WebSocket client to the connected clients list.
     */
    fun attachSession(session: WebSocketSession): Boolean {
        return sessions.add(session)
    }

    /**
     * Detaches an connected client from the list of connected clients list.
     */
    suspend fun detachSession(
        session: WebSocketSession, reason: (() -> CloseReason)? = null
    ): Boolean {
        if (sessions.remove(session) && session.isActive) {
            session.close(reason?.invoke() ?: ABNORMAL_CLOSE)
            return true
        }
        return false
    }

    suspend fun writePacket(session: WebSocketSession, frame: Frame) {
        try {
            session.send(frame.copy())
        } catch (e: Throwable) {
            try {
                session.close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, e.toString()))
            } catch (_: ClosedSendChannelException) {
                // due to use of CopyOnWriteArrayList we must ignore it
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun writePacket(session: WebSocketSession, content: Map<*, *>) {
        writePacket(session, Frame.Text(core.objectMapper.writeValueAsString(content)))
    }

}