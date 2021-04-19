package me.devnatan.katan.webserver.util

import me.devnatan.katan.webserver.websocket.message.WebSocketMessage
import me.devnatan.katan.webserver.websocket.message.WebSocketMessageImpl

suspend inline fun WebSocketMessage.respond(
    op: Int, vararg content:
    Pair<String, Any>
) {
    session.send(WebSocketMessageImpl(op, mapOf(*content), session))
}

suspend inline fun WebSocketMessage.respond(vararg content: Pair<String, Any>) {
    session.send(WebSocketMessageImpl(op, mapOf(*content), session))
}