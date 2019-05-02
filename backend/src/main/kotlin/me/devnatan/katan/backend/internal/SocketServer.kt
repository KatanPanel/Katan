package me.devnatan.katan.backend.internal

import io.ktor.http.cio.websocket.CloseReason
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.close
import kotlinx.coroutines.channels.ClosedSendChannelException
import me.devnatan.katan.backend.message.Message
import me.devnatan.katan.backend.message.frame

class SocketServer {

    private val clients = mutableListOf<WebSocketSession>()

    fun connect(socket: WebSocketSession) {
        clients.add(socket)
    }

    fun disconnect(socket: WebSocketSession) {
        clients.remove(socket)
    }

    suspend fun broadcast(message: String) {
        clients.send(Frame.Text(message))
    }

    suspend fun broadcast(message: Message) {
        clients.send(message.frame)
    }

    private suspend fun List<WebSocketSession>.send(frame: Frame) {
        forEach {
            try {
                it.send(frame.copy())
            } catch (e: Throwable) {
                try {
                    it.close(CloseReason(CloseReason.Codes.PROTOCOL_ERROR, e.toString()))
                } catch (ignore: ClosedSendChannelException) { }
            }
        }
    }

}