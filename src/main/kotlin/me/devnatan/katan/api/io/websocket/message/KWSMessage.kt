package me.devnatan.katan.api.io.websocket.message

import io.ktor.http.cio.websocket.WebSocketSession

/**
 * @param TData return type of message content
 */
interface KWSMessage<out TData : Any> {

    companion object Codes {

        const val SERVER_CREATE = "server_create"
        const val SERVER_START  = "server_start"
        const val SERVER_STOP   = "server_stop"
        const val SERVER_LOG    = "server_log"
        const val SERVER_ATTACH = "server_attach"

    }

    /**
     * Operation code of the message.
     * @see Codes
     */
    val id: String

    /**
     *  Content of the received message.
     */
    val content: TData

    /**
     * Session which sent message.
     */
    val session: WebSocketSession

}