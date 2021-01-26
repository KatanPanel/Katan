package me.devnatan.katan.core

import br.com.devsrsouza.eventkt.scopes.BaseEventScope
import me.devnatan.katan.api.event.Event
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal class EventBus : BaseEventScope() {

    companion object {

        val logger: Logger = LoggerFactory.getLogger(EventBus::class.java)

    }

    override fun publish(value: Any) {
        require(value is Event) { "Cannot publish non-Event objects." }
        publishLocal(value)
        logger.debug("Published ${value::class.simpleName}.")
    }

}