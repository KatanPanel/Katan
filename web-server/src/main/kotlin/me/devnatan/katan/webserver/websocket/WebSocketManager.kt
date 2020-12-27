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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.DATA_KEY
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.OP_KEY
import me.devnatan.katan.webserver.websocket.handler.WebSocketHandler
import me.devnatan.katan.webserver.websocket.message.WebSocketMessage
import me.devnatan.katan.webserver.websocket.message.WebSocketMessageImpl
import me.devnatan.katan.webserver.websocket.session.WebSocketSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.channels.ClosedChannelException
import java.util.concurrent.ConcurrentLinkedQueue

class WebSocketManager {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(WebSocketManager::class.java)
    }

    val objectMapper = jacksonObjectMapper().apply {
        deactivateDefaultTyping()
        disable(SerializationFeature.INDENT_OUTPUT)
        enable(SerializationFeature.CLOSE_CLOSEABLE)
        propertyNamingStrategy = PropertyNamingStrategy.KEBAB_CASE
    }

    private val sessions = ConcurrentLinkedQueue<WebSocketSession>()
    private val handlers = mutableListOf<WebSocketHandler>()
    private val eventbus = LocalEventScope().asSimple()
    private val scope = CoroutineScope(Dispatchers.IO + CoroutineName("Katan::WSManager"))
    private val mutex = Mutex()

    init {
        scope.launch {
            eventbus.listen<WebSocketMessage>().collect { message ->
                for (handler in handlers) {
                    val mappings = handler.mappings
                    if (!mappings.containsKey(message.op))
                        continue

                    // prevents the exception from being thrown so as not to propagate
                    // to collector it and cancel all subsequent events.
                    runCatching {
                        mappings.getValue(message.op).invoke(message)
                    }.onFailure {
                        logger.warn("Failed to invoke ${message.op}: $it")
                    }
                }
            }
        }
    }

    /**
     * Closes and removes all active [WebSocketSession],
     * clears all [WebSocketHandler] and cancels all [Job]s linked to this manager.
     */
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

    /**
     * Emits a message to all registered handlers that
     * targets the operation code for that [WebSocketMessage].
     */
    private fun emitEvent(event: WebSocketMessage) {
        eventbus.publish(event)
    }

    /**
     * Registers all specified [handlers].
     */
    fun registerEventHandler(vararg handlers: WebSocketHandler): WebSocketManager {
        synchronized(this.handlers) {
            this.handlers.addAll(handlers)
        }
        return this
    }

    /**
     * Unregisters a [WebSocketHandler].
     */
    fun unregisterEventHandler(handler: WebSocketHandler) {
        synchronized(handlers) {
            handlers.remove(handler)
        }
    }

    /**
     * Attach the new incoming [WebSocketSession] to the connected clients list.
     */
    suspend fun attachSession(session: WebSocketSession): Boolean {
        return mutex.withLock { sessions.add(session) }
    }

    /**
     * Closes a [WebSocketSession] and detaches it from the list of connected clients.
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

    /**
     * Reads a packet sent by a [WebSocketSession], turns it into a [WebSocketMessage]
     * and sends it to all [WebSocketHandler] that target the operation code for that packet.
     */
    fun readPacket(session: WebSocketSession, packet: Frame.Text) {
        val data = objectMapper.readValue<Map<String, Any>>(packet.readText())
        emitEvent(
            WebSocketMessageImpl(
                data.getValue(OP_KEY) as Int,
                data.getValue(DATA_KEY) as Map<String, Any>,
                session
            )
        )
    }

}