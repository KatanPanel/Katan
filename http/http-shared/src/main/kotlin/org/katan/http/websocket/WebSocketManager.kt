package org.katan.http.websocket

import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager
import java.nio.channels.ClosedChannelException
import java.util.Collections
import kotlin.reflect.jvm.jvmName

class WebSocketManager : CoroutineScope by CoroutineScope(
    SupervisorJob() + CoroutineName(WebSocketManager::class.jvmName)
) {

    companion object {
        private val logger = LogManager.getLogger(WebSocketManager::class.java)
        val json = Json {
            ignoreUnknownKeys = true
        }
    }

    private val sessions = Collections.synchronizedSet<WebSocketSession>(linkedSetOf())
    private val generatedId = atomic(0)
    private val handlers = mutableMapOf<WebSocketOp, MutableList<WebSocketPacketEventHandler>>()

    suspend fun connect(connection: DefaultWebSocketServerSession) {
        val session = WebSocketSession(generatedId.getAndIncrement(), connection)
        attach(session)
        logger.info("New WebSocket session ${session.id} connected @ ${session.connection.call.request.local.remoteHost}")

        try {
            for (frame in connection.incoming) {
                if (frame !is Frame.Text) continue

                val packet = try {
                    json.decodeFromString<WebSocketPacket>(frame.readText())
                } catch (e: SerializationException) {
                    logger.error("Failed to read WebSocket packet.", e)
                    continue
                }

                logger.info("Received packet: $packet")

                val op = packet.op
                logger.info("Op: $op")

                val data = packet.data
                logger.info("data: $data")
                packetReceived(packet, session)
            }
        } catch (e: Throwable) {
            error(e, connection)
        } finally {
            logger.debug("WebSocket session ${session.id} closed")
            detach(session)
        }
    }

    private suspend fun packetReceived(
        packet: WebSocketPacket,
        session: WebSocketSession
    ) {
        val context = WebSocketPacketContext(packet, session)

        handlers[packet.op]?.forEach { handler ->
            handler.apply {
                context.handle()
            }
        }
    }

    private suspend fun error(error: Throwable, session: DefaultWebSocketServerSession) {
        if (error is ClosedReceiveChannelException) {
            logger.debug("WebSocket session receive channel closed: ${session.closeReason.await()}")
            return
        }

        logger.error(
            "WebSocket session handling uncaught error: ${session.closeReason.await()}",
            error
        )
    }

    private fun attach(session: WebSocketSession) {
        sessions.add(session)
    }

    private suspend fun detach(session: WebSocketSession) {
        try {
            close(session)
        } catch (_: ClosedChannelException) {
        }

        sessions.remove(session)
    }

    private suspend fun close(session: WebSocketSession) {
        session.connection.close()
    }

    fun register(op: WebSocketOp, handler: WebSocketPacketEventHandler) {
        val name = "$op-${handler::class.simpleName ?: "unknown-websocket-handler"}"
        val job = Job().apply {
            invokeOnCompletion {
                logger.debug(
                    "WebSocket coroutine scope \"${this[CoroutineName]}\" finished",
                    it
                )
            }
        }

        handler.coroutineContext = job + CoroutineName(name)
        handlers.computeIfAbsent(op) { mutableListOf() }.add(handler)
        logger.debug("WebSocket handler registered: $name")
    }
}
