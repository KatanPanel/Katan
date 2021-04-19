package me.devnatan.katan.webserver.websocket.handlers

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import me.devnatan.katan.api.server.DefaultServerCommandOptions
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.getServerOrNull
import me.devnatan.katan.api.server.isActive
import me.devnatan.katan.webserver.util.respond
import me.devnatan.katan.webserver.websocket.WebSocketManager
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_LOGS_FINISHED
import me.devnatan.katan.webserver.websocket.message.WebSocketMessage
import me.devnatan.katan.webserver.websocket.session.KtorWebSocketSession
import java.time.Duration
import me.devnatan.katan.webserver.websocket.WebSocketOpCode
import me.devnatan.katan.webserver.websocket.WebSocketOpCode.SERVER_LOGS_STARTED

const val SERVER_ID_KEY = "server-id"
const val SERVER_STOP_TIMEOUT_KEY = "timeout"
const val SERVER_EXEC_INPUT_KEY = "input"
const val SERVER_STATS_KEY = "stats"
const val SERVER_LOG_KEY = "log"

/**
 * Returns the [Server.id] contained in the message content  or `null` if the
 * message content is null or the identification number is not present.
 */
private val WebSocketMessage.serverId: Int?
    inline get() = content?.get(SERVER_ID_KEY) as? Int

/**
 * Returns a [Server] obtained through the [serverId].
 */
// TODO: handle missing data
fun WebSocketManager.receiveServer(message: WebSocketMessage): Server {
    val id = message.serverId ?: error("Missing server id")
    return katan.serverManager.getServerOrNull(id) ?: error("Server not found")
}

/**
 * Called when the client requests the backend to start a [Server].
 * Operation code: [WebSocketOpCode.SERVER_START]
 *
 * Payload:
 * [SERVER_ID_KEY]: the server id
 */
suspend inline fun WebSocketManager.wsServerStart(message: WebSocketMessage) {
    // TODO: handle errors
    katan.serverManager.startServer(receiveServer(message))
}

/**
 * Called when the client requests the backend to stop a [Server].
 * Operation code: [WebSocketOpCode.SERVER_STOP]
 *
 * Payload:
 * [SERVER_ID_KEY]: the server id
 * [SERVER_STOP_TIMEOUT_KEY]: time until force to kill the server (in seconds)
 */
suspend inline fun WebSocketManager.wsServerStop(message: WebSocketMessage) {
    val server = receiveServer(message)
    val timeout = message.content?.get(SERVER_STOP_TIMEOUT_KEY)
    if (timeout == null) {
        // TODO: handle errors
        katan.serverManager.stopServer(server)
        return
    }

    // TODO: handle errors
    katan.serverManager.stopServer(
        server, Duration.ofSeconds(
            timeout
                .toString().toLong()
        )
    )
}

/**
 * The client sent a request to the backend to receive stats (CPU,
 * memory, network, etc.) information of a [Server].
 * Operation code: [WebSocketOpCode.SERVER_STATS]
 *
 * Payload:
 * [SERVER_ID_KEY]: the server id
 *
 * @see me.devnatan.katan.api.server.ServerStats
 */
suspend inline fun WebSocketManager.wsServerStats(message: WebSocketMessage) {
    val server = receiveServer(message)

    // TODO: handle errors
    val stats = katan.serverManager.getServerStats(server)

    message.session.launch(NonCancellable + Dispatchers.IO) {
        message.respond(
            SERVER_ID_KEY to server.id,
            SERVER_STATS_KEY to stats
        )
    }
}

/**
 * The client sent a request to the backend to receive stats (CPU,
 * memory, network, etc.) information of a [Server].
 * Operation code: [WebSocketOpCode.SERVER_STATS_START_STREAMING]
 *
 * Unlike the single order [wsServerStats], this operation code is a
 * continuous channel of messages and will constantly send the customer the
 * data he requested until it is canceled using [wsServerStatsStreamingCancel].
 *
 * Payload:
 * [SERVER_ID_KEY]: the server id
 *
 * @see me.devnatan.katan.api.server.ServerStats
 * @see wsServerStatsStreamingCancel
 */
suspend inline fun WebSocketManager.wsServerStatsStreamingStart(
    message: WebSocketMessage
) {
    require(message.session is KtorWebSocketSession)

    val server = receiveServer(message)
    val session = message.session as KtorWebSocketSession

    val job = session.launch(Dispatchers.IO, CoroutineStart.LAZY) {
        katan.serverManager.receiveServerStats(server).onStart {
            message.respond(SERVER_ID_KEY to server.id)
        }.collect { stats ->
            message.respond(
                SERVER_ID_KEY to server.id,
                SERVER_STATS_KEY to stats
            )
        }
    }

    session.startServerStatsStreaming(job)
}

/**
 * Cancels the previously opened channel for receiving data from an
 * [Server] using [wsServerStatsStreamingStart].
 *
 * Operation code: [WebSocketOpCode.SERVER_STATS_CANCEL_STREAMING]
 *
 * Payload:
 * [SERVER_ID_KEY]: the server id
 *
 * @see me.devnatan.katan.api.server.ServerStats
 * @see wsServerStatsStreamingStart
 */
suspend inline fun WebSocketManager.wsServerStatsStreamingCancel(
    message: WebSocketMessage
) {
    require(message.session is KtorWebSocketSession)

    val server = receiveServer(message)
    val session = message.session as KtorWebSocketSession

    if (session.cancelServerStatsStreaming())
        message.respond(SERVER_ID_KEY to server.id)
}

/**
 * Continuously sends registration information from the server, with each new
 * registration that same registration is sent to the client until it is
 * canceled with the end of the connection.
 *
 * This command works with three different opcodes:
 *
 * - [WebSocketOpCode.SERVER_LOGS_STARTED]
 *      when the shipping process starts, at this stage the client must be
 *      prepared to receive the records.
 *
 * - [WebSocketOpCode.SERVER_LOGS]:
 *      the records are being sent to the client.
 *
 * - [WebSocketOpCode.SERVER_LOGS_FINISHED]:
 *      the channel was closed, possibly due to a manual cancellation by the
 *      client, disconnection from the client, or the [Server] was interrupted.
 *
 * Operation code: [WebSocketOpCode.SERVER_LOGS]
 *
 * Payload:
 * [SERVER_ID_KEY]: the server id
 * [SERVER_LOG_KEY]: the contents of the record (in [SERVER_LOGS] stage)
 */
suspend inline fun WebSocketManager.wsServerLogs(message: WebSocketMessage) {
    val server = receiveServer(message)

    message.session.launch(Dispatchers.IO) {
        katan.serverManager.receiveServerLogs(server).onStart {
            message.respond(SERVER_LOGS_STARTED, SERVER_ID_KEY to server.id)
        }.onCompletion {
            message.respond(SERVER_LOGS_FINISHED, SERVER_ID_KEY to server.id)
        }.collect {
            message.respond(SERVER_ID_KEY to server.id, SERVER_LOG_KEY to it)
        }
    }
}

/**
 * Executes a command sent by the client in the container of a [Server].
 *
 * Operation code: [WebSocketOpCode.SERVER_EXEC]
 *
 * Payload:
 * [SERVER_ID_KEY]: the server id
 * [SERVER_EXEC_INPUT_KEY]: the command to be executed
 */
suspend inline fun WebSocketManager.wsServerExec(message: WebSocketMessage) {
    val server = receiveServer(message)

    // TODO: server inactive response
    if (!server.state.isActive())
        return

    // TODO: handle missing input
    val command = message.content?.get(SERVER_EXEC_INPUT_KEY) ?: return

    // TODO: handle exceptions
    katan.serverManager.runServerCommand(
        server,
        command.toString(),
        DefaultServerCommandOptions
    )
}