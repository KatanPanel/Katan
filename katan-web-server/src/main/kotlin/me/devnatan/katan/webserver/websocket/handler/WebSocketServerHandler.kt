package me.devnatan.katan.webserver.websocket.handler

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import me.devnatan.katan.api.Katan
import me.devnatan.katan.api.server.DefaultServerCommandOptions
import me.devnatan.katan.api.server.getServerOrNull
import me.devnatan.katan.api.server.isActive
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_ATTACH
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_EXEC
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_LOGS
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_LOGS_FINISHED
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_LOGS_STARTED
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_START
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_STATS
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_STATS_CANCEL_STREAMING
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_STATS_START_STREAMING
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_STOP
import me.devnatan.katan.webserver.websocket.message.WebSocketMessage
import me.devnatan.katan.webserver.websocket.session.KtorWebSocketSession
import java.time.Duration

private const val SERVER_ID_KEY = "server-id"
private const val SERVER_EXEC_INPUT_KEY = "input"

class WebSocketServerHandler(katan: Katan) : WebSocketHandler() {

    init {
        handle(SERVER_START) {
            val server = katan.serverManager.getServerOrNull(serverId) ?: return@handle
            katan.serverManager.startServer(server)
        }

        handle(SERVER_STOP) {
            val server = katan.serverManager.getServerOrNull(serverId) ?: return@handle
            katan.serverManager.stopServer(server, Duration.ofSeconds(content?.get("timeout")?.toString()?.toLongOrNull() ?: 10))
        }

        handle(SERVER_STATS) {
            val server = katan.serverManager.getServerOrNull(serverId) ?: return@handle

            session.launch(NonCancellable + Dispatchers.IO) {
                val stats = katan.serverManager.getServerStats(server)
                respond(SERVER_ID_KEY to serverId, "stats" to stats)
            }
        }

        handle(SERVER_STATS_START_STREAMING) {
            val server = katan.serverManager.getServerOrNull(serverId) ?: return@handle

            // it is necessary to launch in the scope of the session
            // itself, in case there is a finalization there is no leak.
            val job = session.launch(Dispatchers.IO + CoroutineName("Katan WebSocket Server (${server.id}) Stats Streaming")) {
                katan.serverManager.receiveServerStats(server).collect { stats ->
                    respond(SERVER_ID_KEY to serverId, "stats" to stats)
                }
            }

            if (session is KtorWebSocketSession) {
                val ktor = session as KtorWebSocketSession
                job.invokeOnCompletion {
                    it?.printStackTrace()
                    synchronized(ktor.serverStatsStreaming) {
                        ktor.serverStatsStreaming.remove(server.id)
                    }
                }

                synchronized (ktor.serverStatsStreaming) {
                    ktor.serverStatsStreaming[server.id] = job
                }
            }
        }

        handle(SERVER_STATS_CANCEL_STREAMING) {
            if (session !is KtorWebSocketSession)
                return@handle

            val server = katan.serverManager.getServerOrNull(serverId) ?: return@handle
            val ktor = session as KtorWebSocketSession

            if (!ktor.serverStatsStreaming.containsKey(server.id))
                return@handle

            val job = ktor.serverStatsStreaming.getValue(server.id)
            job.cancelAndJoin()

            synchronized(ktor.serverStatsStreaming) {
                ktor.serverStatsStreaming.remove(server.id)
            }
        }

        handle(SERVER_LOGS) {
            val server = katan.serverManager.getServerOrNull(serverId) ?: return@handle
            session.launch(Dispatchers.IO) {
                katan.serverManager.receiveServerLogs(server).onStart {
                    respond(SERVER_LOGS_STARTED, SERVER_ID_KEY to serverId)
                }.onCompletion {
                    respond(SERVER_LOGS_FINISHED, SERVER_ID_KEY to serverId)
                }.collect {
                    respond(SERVER_LOGS, SERVER_ID_KEY to serverId, "log" to it)
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

            val command = (content ?: return@handle).getValue(SERVER_EXEC_INPUT_KEY) as String
            katan.serverManager.runServerCommand(server, command, DefaultServerCommandOptions)
        }
    }

}

val WebSocketMessage.serverId: Int
    get() = content!!.getValue(SERVER_ID_KEY) as Int