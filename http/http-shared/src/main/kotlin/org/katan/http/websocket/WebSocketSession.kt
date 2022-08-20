package org.katan.http.websocket

import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.websocket.Frame
import kotlinx.serialization.encodeToString

class WebSocketSession internal constructor(
    val id: Int,
    val connection: DefaultWebSocketServerSession
) {

    suspend fun send(message: WebSocketPacket) {
        connection.outgoing.send(Frame.Text(
            WebSocketManager.json.encodeToString(message)
        ))
    }

}