package me.devnatan.katan.backend.message.handler

import me.devnatan.katan.backend.message.IncomingMessage

@FunctionalInterface
interface MessageHandler {

    suspend fun handle(message: IncomingMessage)

}