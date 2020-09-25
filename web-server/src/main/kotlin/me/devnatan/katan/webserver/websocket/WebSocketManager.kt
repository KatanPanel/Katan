package me.devnatan.katan.webserver.websocket

import br.com.devsrsouza.eventkt.listen
import br.com.devsrsouza.eventkt.scopes.LocalEventScope
import br.com.devsrsouza.eventkt.scopes.asSimple
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.devnatan.katan.webserver.websocket.handler.WebSocketHandler
import me.devnatan.katan.webserver.websocket.message.MutableWebSocketMessage
import me.devnatan.katan.webserver.websocket.message.WebSocketMessage
import me.devnatan.katan.webserver.websocket.message.WebSocketMessageImpl
import me.devnatan.katan.webserver.websocket.session.WebSocketSession
import java.nio.channels.ClosedChannelException
import java.util.concurrent.CopyOnWriteArrayList

class WebSocketManager {

    private val sessions = CopyOnWriteArrayList<WebSocketSession>()
    private val handlers = mutableListOf<WebSocketHandler<WebSocketMessage, *>>()
    private val eventbus = LocalEventScope().asSimple()
    private val scope = CoroutineScope(Dispatchers.IO + CoroutineName("Katan::WebSocketManager"))
    private val mutex = Mutex()

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

    suspend fun close() {
        mutex.withLock(sessions) {
            val iter = sessions.iterator()
            while (iter.hasNext()) {
                detachSession(iter.next(), false)
            }
        }
        eventbus.cancel()
        scope.cancel()
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
    suspend fun attachSession(session: WebSocketSession): Boolean {
        return mutex.withLock { sessions.add(session) }
    }

    /**
     * Detaches an connected client from the list of connected clients list.
     */
    suspend fun detachSession(session: WebSocketSession, remove: Boolean = true): Boolean {
        try {
            session.close()
        } catch (_: ClosedChannelException) {
        }

        return if (remove) mutex.withLock(sessions) {
            sessions.remove(session)
        } else remove
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun writePacket(message: WebSocketMessage) {
        try {
            message.session.send(message.run {
                WebSocketMessageImpl(op, Frame.Text(Json.encodeToString(content)).copy(), session)
            })
        } catch (e: Throwable) {
            detachSession(message.session)
        }
    }

}