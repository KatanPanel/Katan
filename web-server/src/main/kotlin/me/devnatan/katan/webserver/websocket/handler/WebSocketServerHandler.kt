package me.devnatan.katan.webserver.websocket.handler

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import me.devnatan.katan.api.Katan
import me.devnatan.katan.api.server.DefaultServerCommandOptions
import me.devnatan.katan.api.server.getServerOrNull
import me.devnatan.katan.api.server.isActive
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_ATTACH
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_EXEC
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_INFO
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_LOGS
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_LOGS_FINISHED
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_LOGS_STARTED
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_STATS
import me.devnatan.katan.webserver.websocket.message.WebSocketMessage

private const val SERVER_ID_KEY = "server-id"
private const val SERVER_EXEC_INPUT_KEY = "input"

class WebSocketServerHandler(katan: Katan) : WebSocketHandler() {

    init {
        handle(SERVER_INFO) {
            val server = katan.serverManager.getServerOrNull(serverId) ?: return@handle
            katan.serverManager.inspectServer(server)

            respond(
                mapOf(
                    SERVER_ID_KEY to serverId,
                    "data" to server
                )
            )
        }

        handle(SERVER_STATS) {
            val server = katan.serverManager.getServerOrNull(serverId) ?: return@handle
            val stats = katan.serverManager.getServerStats(server)

            respond(
                mapOf(
                    SERVER_ID_KEY to serverId,
                    "stats" to stats
                )
            )
        }

        handle(SERVER_LOGS) {
            val server = katan.serverManager.getServerOrNull(serverId) ?: return@handle
            katan.launch(Dispatchers.IO) {
                katan.serverManager.receiveServerLogs(server).onStart {
                    respond(SERVER_LOGS_STARTED, mapOf(SERVER_ID_KEY to serverId))
                }.onCompletion {
                    respond(SERVER_LOGS_FINISHED, mapOf(SERVER_ID_KEY to serverId))
                }.collect {
                    respond(SERVER_LOGS, mapOf(
                        SERVER_ID_KEY to serverId,
                        "log" to it
                    ))
                }
            }
        }

        handle(SERVER_ATTACH) {
            val server = katan.serverManager.getServerOrNull(serverId) ?: return@handle
            if (!server.state.isActive())
                return@handle

            // TODO: ...
        }

        handle(SERVER_EXEC) {
            val server = katan.serverManager.getServerOrNull(serverId) ?: return@handle
            if (!server.state.isActive())
                return@handle

            val command = content.getValue(SERVER_EXEC_INPUT_KEY) as String
            katan.serverManager.runServerCommand(server, command, DefaultServerCommandOptions)
        }
    }

}

val WebSocketMessage.serverId: Int
    get() = content.getValue(SERVER_ID_KEY) as Int