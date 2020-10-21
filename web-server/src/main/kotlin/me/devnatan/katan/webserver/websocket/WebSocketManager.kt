package me.devnatan.katan.webserver.websocket

import br.com.devsrsouza.eventkt.listen
import br.com.devsrsouza.eventkt.scopes.LocalEventScope
import br.com.devsrsouza.eventkt.scopes.asSimple
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.devnatan.katan.webserver.websocket.handler.WebSocketHandler
import me.devnatan.katan.webserver.websocket.message.WebSocketMessage
import me.devnatan.katan.webserver.websocket.message.WebSocketMessageImpl
import me.devnatan.katan.webserver.websocket.session.WebSocketSession
import java.nio.channels.ClosedChannelException
import java.util.concurrent.CopyOnWriteArrayList

class WebSocketManager {

    val objectMapper = jacksonObjectMapper().apply {
        deactivateDefaultTyping()
        disable(SerializationFeature.INDENT_OUTPUT)
        enable(SerializationFeature.CLOSE_CLOSEABLE)
        propertyNamingStrategy = PropertyNamingStrategy.KEBAB_CASE
    }

    private val sessions = CopyOnWriteArrayList<WebSocketSession>()
    private val handlers = mutableListOf<WebSocketHandler>()
    private val eventbus = LocalEventScope().asSimple()
    private val scope = CoroutineScope(Dispatchers.IO + CoroutineName("Katan::WebSocketManager"))
    private val mutex = Mutex()

    init {
        eventbus.listen<WebSocketMessage>().onEach { message ->
            for (handler in handlers) {
                val mappings = handler.mappings
                if (!mappings.containsKey(message.op))
                    continue

                mappings.getValue(message.op).invoke(message)
            }
        }.launchIn(scope)
    }

    suspend fun close() {
        mutex.withLock(sessions) {
            val iter = sessions.iterator()
            while (iter.hasNext()) {
                detachSession(iter.next(), false)
                iter.remove()
            }
        }

        synchronized(handlers) {
            handlers.clear()
        }

        if (eventbus.coroutineContext.isActive)
            eventbus.cancel()

        scope.cancel()
    }

    fun emitEvent(event: WebSocketMessage) {
        eventbus.publish(event)
    }

    fun registerEventHandler(vararg handlers: WebSocketHandler): WebSocketManager {
        synchronized(this.handlers) {
            this.handlers.addAll(handlers)
        }
        return this
    }

    fun unregisterEventHandler(handler: WebSocketHandler) {
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
            if (!remove)
                return true
        } catch (_: ClosedChannelException) {
        }

        if (remove) {
            return mutex.withLock {
                sessions.remove(session)
            }
        }

        return false
    }

    fun readPacket(session: WebSocketSession, packet: Frame.Text) {
        val data = objectMapper.readValue<Map<String, Any>>(packet.readText())
        emitEvent(
            WebSocketMessageImpl(
                data.getValue("op") as Int,
                data.getValue("d") as Map<String, Any>,
                session
            )
        )
    }

}