package me.devnatan.katan.api.io.websocket.message

import io.ktor.http.cio.websocket.WebSocketSession

class KWSServerMessage(
    id: String,
    session: WebSocketSession,
    content: Message
) : KWSBaseMessage<KWSServerMessage.Message>(id, session, content) {

    interface Message {

        val serverId: UInt

    }

}