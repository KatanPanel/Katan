package me.devnatan.katan.api.io.websocket

interface WebSocketMessage {

    /**
     * Operation code of the message.
     * @see WebSocketOpCode
     */
    val op: Int

    /**
     * Content of the received message.
     */
    val content: Any

    /**
     * Session which sent message.
     */
    val session: WebSocketSession

}