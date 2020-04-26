package me.devnatan.katan.api.io.websocket.message

import io.ktor.http.cio.websocket.WebSocketSession

class KWSAttachMessage(
    id: String,
    session: WebSocketSession,
    content: Message
) : KWSBaseMessage<KWSAttachMessage.Message>(id, session, content) {

    interface Message : KWSServerMessage.Message {

        val input: String

    }

}