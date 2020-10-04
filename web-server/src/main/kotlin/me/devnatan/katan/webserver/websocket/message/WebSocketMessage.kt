package me.devnatan.katan.webserver.websocket.message

import me.devnatan.katan.webserver.websocket.session.WebSocketSession

interface WebSocketMessage {

    /**
     * Operation code of the message.
     * @see me.devnatan.katan.webserver.websocket.WebSocketOpCode
     */
    val op: Int

    /**
     * Content of the received message.
     */
    val content: Map<String, Any>

    /**
     * Session which sent message.
     */
    val session: WebSocketSession

}

data class WebSocketMessageImpl(
    override val op: Int,
    override val content: Map<String, Any>,
    override val session: WebSocketSession
) : WebSocketMessage