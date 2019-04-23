package me.devnatan.katan.frontend.socket

import me.devnatan.katan.frontend.DynamicBlock
import me.devnatan.katan.frontend.EmptyBlock
import me.devnatan.katan.frontend.logger.Logger
import me.devnatan.katan.frontend.measureTimeMillis
import org.w3c.dom.WebSocket

class Socket {

    private val logger = Logger("GatewaySocket")
    private lateinit var ws: WebSocket

    var onConnect: EmptyBlock? = null
    var onClose: EmptyBlock? = null
    var onError: EmptyBlock? = null
    var onMessage: DynamicBlock? = null

    var state: SocketState = SocketState.CLOSED
    var connectionTime: Long = -1

    fun connect() {
        connectionTime = measureTimeMillis {
            state = SocketState.CONNECTING
            ws = WebSocket("ws://127.0.0.1:8080")
        }

        ws.onopen = {
            state = SocketState.OPEN
            onConnect?.invoke()
            logger.fine("[CONNECTED] ${connectionTime}ms")
        }

        ws.onmessage = {
            onMessage?.invoke(it.data)
        }

        ws.onclose = {
            state = SocketState.CLOSED
            onClose?.invoke()
            logger.err("[CLOSE]")
        }

        ws.onerror = {
            onError?.invoke()
            logger.warn("[ERROR]")
        }
    }

    fun send(message: String) {
        if (state != SocketState.OPEN)
            throw IllegalStateException("Socket isn't connected")

        ws.send(message)
    }

}