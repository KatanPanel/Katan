package me.devnatan.katan.core.manager

import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.close
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.launch
import me.devnatan.katan.Katan
import me.devnatan.katan.api.io.websocket.message.KWSLogMessage
import me.devnatan.katan.api.io.websocket.message.KWSMessage
import me.devnatan.katan.api.io.websocket.message.KWSServerMessage
import me.devnatan.katan.core.handler.DefaultWSHandler
import me.devnatan.katan.core.util.toString
import org.greenrobot.eventbus.Subscribe
import org.slf4j.LoggerFactory
import java.util.concurrent.CopyOnWriteArrayList

class WSManager(internal val core: Katan) {

    private val logger  = LoggerFactory.getLogger(WSManager::class.java)!!
    private val clients = CopyOnWriteArrayList<WebSocketSession>()
    private val handler = DefaultWSHandler(core)

    /**
     * Attach the new incoming WebSocket client to the connected clients list.
     */
    fun attach(session: WebSocketSession) {
        clients.add(session)
        logger.debug("Session $session connected")
    }

    /**
     * Detaches an connected client from the list of connected clients list.
     */
    fun detach(session: WebSocketSession) {
        clients.remove(session)
        logger.debug("Session $session disconnected")
    }

    // internal
    @Subscribe
    fun onMessage(message: KWSMessage<*>) {
        GlobalScope.launch {
            when (message.id) {
                KWSMessage.SERVER_CREATE -> handler.onServerCreate(message as KWSServerMessage)
                KWSMessage.SERVER_START -> handler.onServerStart(message as KWSServerMessage)
                KWSMessage.SERVER_STOP -> handler.onServerStop(message as KWSServerMessage)
                KWSMessage.SERVER_LOG -> handler.onServerLog(message as KWSLogMessage)
                KWSMessage.SERVER_ATTACH -> handler.onServerAttach(message as KWSServerMessage)
            }
            logger.debug("Session (${message.session}) message: $message")
        }
    }

    private suspend fun send(session: WebSocketSession, frame: Frame) {
        try {
            session.send(frame.copy())
        } catch (e: Throwable) {
            try {
                session.close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, e.toString()))
            } catch (ignore: ClosedSendChannelException) { }
        }
    }

    internal suspend fun send(session: WebSocketSession, content: Map<*, *>) {
        send(session, Frame.Text(content.toString(core.jsonMapper)!!))
    }

}