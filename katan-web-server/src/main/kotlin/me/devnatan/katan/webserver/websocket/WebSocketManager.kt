package me.devnatan.katan.webserver.websocket

import br.com.devsrsouza.eventkt.EventScope
import br.com.devsrsouza.eventkt.listen
import br.com.devsrsouza.eventkt.scopes.LocalEventScope
import br.com.devsrsouza.eventkt.scopes.asSimple
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.devnatan.katan.api.event.server.ServerStateChangeEvent
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.DATA_KEY
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.INVALID
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.OP_KEY
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_UPDATED
import me.devnatan.katan.webserver.websocket.handler.WebSocketHandler
import me.devnatan.katan.webserver.websocket.message.WebSocketMessage
import me.devnatan.katan.webserver.websocket.message.WebSocketMessageImpl
import me.devnatan.katan.webserver.websocket.session.KtorWebSocketSession
import me.devnatan.katan.webserver.websocket.session.WebSocketSession
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.channels.ClosedChannelException
import java.util.concurrent.ConcurrentLinkedQueue

class WebSocketManager(private val mainEventScope: EventScope) {

    companion object {
        private val logger: Logger =
            LoggerFactory.getLogger(WebSocketManager::class.java)
    }

    private val objectMapper = jacksonObjectMapper().apply {
        deactivateDefaultTyping()
        disable(SerializationFeature.INDENT_OUTPUT)
        disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
        enable(SerializationFeature.CLOSE_CLOSEABLE)
        propertyNamingStrategy = PropertyNamingStrategy.KEBAB_CASE
    }

    private val sessions = ConcurrentLinkedQueue<WebSocketSession>()
    private val handlers = mutableListOf<WebSocketHandler>()
    private val eventbus = LocalEventScope().asSimple()
    private val scope =
        CoroutineScope(Dispatchers.IO + CoroutineName("Katan::WSManager"))
    private val mutex = Mutex()

    fun listen() {
        scope.launch(CoroutineName("Katan WS Message Propagator")) {
            mainEventScope.listen<ServerStateChangeEvent>().collect { event ->
                for (session in sessions) {
                    session.send(
                        WebSocketMessageImpl(
                            SERVER_UPDATED,
                            mapOf(
                                "o" to event.oldState,
                                "n" to event.newState,
                                "server" to event.server
                            ),
                            session
                        )
                    )
                }
            }
        }

        scope.launch(CoroutineName("Katan WS Message Listener")) {
            eventbus.listen<WebSocketMessage>().collect { message ->
                var mapped = false

                for (handler in handlers) {
                    val mappings = handler.mappings
                    if (!mappings.containsKey(message.op))
                        continue

                    mapped = true
                    // prevents the exception from being thrown so as not to propagate
                    // to collector it and cancel all subsequent events.
                    runCatching {
                        mappings.getValue(message.op).invoke(message)
                    }.onFailure { error ->
                        logger.warn("Failed to invoke ${message.op}.")
                        logger.trace(null, error)
                    }
                }

                // invalid operation code
                if (!mapped) {
                    message.session.send(
                        WebSocketMessageImpl(
                            INVALID,
                            mapOf("code" to message.op),
                            message.session
                        )
                    )
                }
            }
        }
    }

    suspend fun handle(session: DefaultWebSocketSession) {
        val impl = KtorWebSocketSession(session) { packet ->
            session.outgoing.send(
                Frame.Text(
                    objectMapper.writeValueAsString(
                        mapOf(
                            OP_KEY to packet.op,
                            DATA_KEY to packet.content
                        )
                    )
                )
            )
        }

        attachSession(impl)

        try {
            for (frame in session.incoming) {
                when (frame) {
                    is Frame.Text -> {
                        val data =
                            objectMapper.readValue<Map<String, Any>>(frame.readText())
                        val op = (data[OP_KEY] ?: break) as Int

                        @Suppress("UNCHECKED_CAST")
                        val content = data[DATA_KEY] as? Map<String, Any>

                        // publish the message to all registered handlers
                        eventbus.publish(
                            WebSocketMessageImpl(
                                op,
                                content,
                                impl
                            )
                        )
                    }
                    else -> {
                        // terminating the connection is the best thing to do
                        // here, we cannot handle unknown (non-text) data for now.
                        logger.debug("Unable to handle $frame frame, closing connection")
                        break
                    }
                }
            }
        } catch (_: ClosedReceiveChannelException) {
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            detachSession(impl)
        }
    }

    /**
     * Closes and removes all active [WebSocketSession],
     * clears all [WebSocketHandler] and cancels all [Job]s linked to this manager.
     */
    suspend fun close() {
        mutex.withLock(sessions) {
            val iterator = sessions.iterator()
            while (iterator.hasNext()) {
                detachSession(iterator.next(), remove = false)
                iterator.remove()
            }
        }

        scope.cancel()

        synchronized(handlers) {
            handlers.clear()
        }
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
    private suspend fun attachSession(session: WebSocketSession): Boolean {
        return mutex.withLock { sessions.add(session) }
    }

    /**
     * Closes a [WebSocketSession] and detaches it from the list of connected clients.
     */
    private suspend fun detachSession(
        session: WebSocketSession,
        remove: Boolean = true
    ): Boolean {
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

}