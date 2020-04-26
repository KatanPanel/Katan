package me.devnatan.katan.api.io.websocket.message

import io.ktor.http.cio.websocket.WebSocketSession

class KWSLogMessage(
    id: String,
    session: WebSocketSession,
    content: Message
) : KWSBaseMessage<KWSLogMessage.Message>(id, session, content) {

    interface Message : KWSServerMessage.Message {

        val input: String

    }

}