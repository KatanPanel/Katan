@file:JvmMultifileClass
package me.devnatan.katan.backend.message.handler

import me.devnatan.katan.api.server.ServerState
import me.devnatan.katan.backend.katan
import me.devnatan.katan.backend.message.*

object ServerHandlerPredicate : MessageHandlerPredicate {

    override fun invoke(m: Message): Boolean {
        return m.contains("server")
    }

}

object StartServerHandler : MessageHandler {

    override suspend fun handle(message: IncomingMessage) {
        if (!message.isCommand("start-server")) return

        katan.serverController.getServer(message["server"] as Int)?.let {
            if (it.state == ServerState.STOPPED)
                it.start {
                    katan.socketController.broadcast(MessageImpl(
                        MessageReason.SERVER_UPDATED,
                        MessageType.COMMAND,
                        mapOf("server" to it)
                    ))
                }
            else
                katan.socketController.broadcast(MessageImpl(
                    MessageReason.SERVER_ALREADY_STARTED,
                    MessageType.MESSAGE,
                    mapOf("server" to it.id)
                ))
        }
    }

}

object StopServerHandler : MessageHandler {

    override suspend fun handle(message: IncomingMessage) {
        if (!message.isCommand("stop-server")) return

        katan.serverController.getServer(message["server"] as Int)?.let {
            if (it.state != ServerState.STOPPED) {
                it.stop {
                    katan.socketController.broadcast(MessageImpl(
                        MessageReason.SERVER_UPDATED,
                        MessageType.COMMAND,
                        mapOf("server" to it)
                    ))
                }
            }
        }
    }

}

object InputServerHandler : MessageHandler {

    override suspend fun handle(message: IncomingMessage) {
        if (!message.isCommand("input-server")) return

        katan.serverController.getServer(message["server"] as Int)?.let {
            if (it.state == ServerState.RUNNING) {
                it.process.write(message["input"] as String)
            }
        }
    }

}