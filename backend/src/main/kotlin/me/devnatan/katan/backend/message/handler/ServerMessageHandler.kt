@file:JvmMultifileClass
package me.devnatan.katan.backend.message.handler

import me.devnatan.katan.backend.katan
import me.devnatan.katan.backend.message.*
import me.devnatan.katan.backend.server.EnumKServerState

object ServerHandlerPredicate : MessageHandlerPredicate {

    override fun invoke(m: Message): Boolean {
        return m.contains("server")
    }

}

object StartServerHandler : MessageHandler {

    override suspend fun handle(message: IncomingMessage) {
        if (!message.isCommand("start-server")) return

        katan.serverManager.getServer((message["server"] as Double).toInt())?.let {
            if (it.state == EnumKServerState.STOPPED)
                it.startAsync {
                    katan.socketServer.broadcast(MessageImpl(
                        MessageReason.SERVER_UPDATED,
                        MessageType.COMMAND,
                        mapOf("server" to it)
                    ))
                }
            else
                katan.socketServer.broadcast(MessageImpl(
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

        katan.serverManager.getServer((message["server"] as Double).toInt())?.let {
            if (it.state != EnumKServerState.STOPPED) {
                it.stop {
                    katan.socketServer.broadcast(MessageImpl(
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

        katan.serverManager.getServer((message["server"] as Double).toInt())?.let {
            if (it.state == EnumKServerState.RUNNING) {
                it.write(message["input"] as String)
            }
        }
    }

}