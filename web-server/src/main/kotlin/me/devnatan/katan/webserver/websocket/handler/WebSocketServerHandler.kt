package me.devnatan.katan.webserver.websocket.handler

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.devnatan.katan.api.Katan
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_STATS
import me.devnatan.katan.webserver.websocket.message.WebSocketMessage

class WebSocketServerHandler(katan: Katan) : WebSocketHandler() {

    init {
        handle(SERVER_STATS) {

        }

        handle(SERVER_STATS) {
            println("handling server stats")

            val server = katan.serverManager.getServer(serverId)

            katan.launch(Dispatchers.IO) {
                katan.serverManager.receiveServerStats(server).collect {
                    respond(
                        mapOf(
                            "server-id" to serverId,
                            "stats" to it
                        )
                    )
                }
            }
        }
    }

}

val WebSocketMessage.serverId: Int
    get() = content.getValue("server-id") as Int