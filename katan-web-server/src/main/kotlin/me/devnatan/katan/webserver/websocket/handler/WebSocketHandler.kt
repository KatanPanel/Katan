package me.devnatan.katan.webserver.websocket.handler

import me.devnatan.katan.webserver.websocket.message.WebSocketMessage
import me.devnatan.katan.webserver.websocket.message.WebSocketMessageImpl

typealias WebSocketMessageHandlerBlock = suspend WebSocketMessage.() -> Unit

abstract class WebSocketHandler {

    val mappings = hashMapOf<Int, WebSocketMessageHandlerBlock>()

}

suspend fun WebSocketMessage.respond(op: Int, vararg content: Pair<String, Any>) {
    session.send(WebSocketMessageImpl(op, mapOf(*content), session))
}

suspend fun WebSocketMessage.respond(vararg content: Pair<String, Any>) {
    respond(this.op, *content)
}

fun WebSocketHandler.handle(target: Int, block: WebSocketMessageHandlerBlock) {
    mappings[target] = block
}