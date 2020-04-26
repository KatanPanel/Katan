package me.devnatan.katan.core.handler

import com.github.dockerjava.api.model.Frame
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.devnatan.katan.Katan
import me.devnatan.katan.api.io.websocket.KWSHandler
import me.devnatan.katan.api.io.websocket.message.KWSLogMessage
import me.devnatan.katan.api.io.websocket.message.KWSMessage
import me.devnatan.katan.api.io.websocket.message.KWSServerMessage
import me.devnatan.katan.api.server.KServer
import java.nio.charset.StandardCharsets

class DefaultWSHandler(private val core: Katan) : KWSHandler {

    private suspend fun sendUpdate(server: KServer, message: KWSMessage<*>) {
        core.webSocketManager.send(
            message.session, mapOf(
                "id" to "server-update",
                "content" to mapOf("server" to server)
            )
        )
    }

    override suspend fun onServerCreate(message: KWSServerMessage) {
        throw NotImplementedError()
    }

    override suspend fun onServerStart(message: KWSServerMessage) {
        sendUpdate(core.serverManager.startServer(message.content.serverId), message)
    }

    override suspend fun onServerStop(message: KWSServerMessage) {
        sendUpdate(core.serverManager.stopServer(message.content.serverId), message)
    }

    override suspend fun onServerLog(message: KWSLogMessage) {
        core.serverManager.logServer(message.content.serverId, message.content.input)
    }

    override suspend fun onServerAttach(message: KWSServerMessage) {
        core.serverManager.attachServer(message.content.serverId, message.session) { frame: Frame ->
            GlobalScope.launch {
                core.webSocketManager.send(
                    message.session, mapOf(
                        "id" to "server-log",
                        "content" to frame.payload.toString(StandardCharsets.UTF_8).trim()
                    )
                )
            }
        }
    }

}