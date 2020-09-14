package me.devnatan.katan.api.io.websocket

typealias WebSocketHandlerMapper<T> = (T).() -> Unit

interface WebSocketHandler<T : Any, R> {

    object NOTHING

    fun next(message: T): R

    fun mappings(): Map<Int, WebSocketHandlerMapper<T>>

}

fun WebSocketHandler<*, *>.defaultMappingsHandler(message: WebSocketMessage): Boolean {
    return mappings().any { it.key == message.op }
}