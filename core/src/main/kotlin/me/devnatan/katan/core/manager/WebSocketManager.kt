package me.devnatan.katan.core.manager

import br.com.devsrsouza.eventkt.listen
import br.com.devsrsouza.eventkt.scopes.LocalEventScope
import br.com.devsrsouza.eventkt.scopes.asSimple
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.transform
import me.devnatan.katan.api.io.websocket.WebSocketHandler
import me.devnatan.katan.api.io.websocket.WebSocketMessage
import me.devnatan.katan.core.Katan
import me.devnatan.katan.core.websocket.MutableWebSocketMessage
import java.nio.channels.ClosedChannelException
import java.util.concurrent.CopyOnWriteArrayList

class WebSocketManager(private val core: Katan) {

    private val sessions = CopyOnWriteArrayList<WebSocketSession>()
    private val handlers = mutableListOf<WebSocketHandler<WebSocketMessage, *>>()
    private val eventbus = LocalEventScope().asSimple()
    private val scope = CoroutineScope(Dispatchers.IO + CoroutineName("Katan::WebSocketManager"))

    init {
        eventbus.listen<WebSocketMessage>().transform { message ->
            for (handler in handlers) {
                val mappings = handler.mappings
                if (!mappings.containsKey(message.op))
                    continue

                if (message is MutableWebSocketMessage) {
                    try {
                        val result = handler.next(message)
                        if (result is WebSocketHandler.NOTHING)
                            continue

                        message.content = result ?: WebSocketHandler.NULL
                    } catch (e: NotImplementedError) {
                    }
                }

                mappings.getValue(message.op).invoke(message)
                emit(message.content)
            }
        }.launchIn(scope)
    }

    fun emitEvent(event: WebSocketMessage) {
        eventbus.publish(event)
    }

    fun registerEventHandler(vararg handlers: WebSocketHandler<WebSocketMessage, *>) {
        synchronized(this.handlers) {
            this.handlers.addAll(handlers)
        }
    }

    fun unregisterEventHandler(handler: WebSocketHandler<WebSocketMessage, *>) {
        synchronized(handlers) {
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
        session: WebSocketSession,
        reason: CloseReason = CloseReason(CloseReason.Codes.NORMAL, ""),
    ): Boolean {
        try {
            session.close(reason)
        } catch (_: ClosedChannelException) {
        }

        return sessions.remove(session)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun writePacket(session: WebSocketSession, packet: Any) {
        try {
            session.send(Frame.Text(core.objectMapper.writeValueAsString(packet)).copy())
        } catch (e: Throwable) {
            detachSession(session, CloseReason(CloseReason.Codes.INTERNAL_ERROR, e.toString()))
        }
    }

}