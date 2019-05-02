package me.devnatan.katan.backend.message

import me.devnatan.katan.backend.message.handler.MessageHandler
import org.slf4j.LoggerFactory

typealias MessageHandlerPredicate = (Message) -> Boolean

class Messenger {

    companion object {

        private val LOGGER = LoggerFactory.getLogger("Messenger")!!

    }

    private val handlers: MutableMap<MessageHandlerPredicate, Array<out MessageHandler>> = mutableMapOf()

    fun addPredicate(predicate: MessageHandlerPredicate, vararg handlers: MessageHandler) {
        this.handlers[predicate] = handlers
        LOGGER.info("${handlers.size} handlers registered in ${predicate::class.simpleName}.")
    }

    suspend fun handle(message: IncomingMessage) {
        handlers.entries.filter {
            it.key(message)
        }.forEach {
            it.value.forEach { h ->
                h.handle(message)
            }
        }
    }

}