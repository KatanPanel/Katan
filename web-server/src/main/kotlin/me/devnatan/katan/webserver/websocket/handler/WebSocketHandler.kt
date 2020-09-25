package me.devnatan.katan.webserver.websocket.handler

import me.devnatan.katan.webserver.websocket.message.WebSocketMessage

typealias WebSocketHandlerMapper<T> = (T).() -> Unit

interface WebSocketHandler<T : WebSocketMessage, R> {

    object NULL

    object NOTHING

    val mappings: Map<Int, WebSocketHandlerMapper<T>>

    fun next(message: T): R {
        throw NotImplementedError()
    }

}