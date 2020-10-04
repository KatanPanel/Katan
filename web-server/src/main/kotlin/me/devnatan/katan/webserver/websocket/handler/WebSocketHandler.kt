package me.devnatan.katan.webserver.websocket.handler

import me.devnatan.katan.webserver.websocket.message.WebSocketMessage

typealias WebSocketMessageHandlerBlock = suspend WebSocketMessage.() -> Unit

abstract class WebSocketHandler {

    val mappings = hashMapOf<Int, WebSocketMessageHandlerBlock>()

}

fun WebSocketHandler.handle(target: Int, block: WebSocketMessageHandlerBlock) {
    mappings[target] = block
}