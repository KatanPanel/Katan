package org.katan.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

interface EventScope : CoroutineScope {

    /**
     * Dispatches an event.
     */
    fun dispatch(event: Event)

    /**
     * Listens as Flow for an event of the given type.
     */
    fun <T : Event> listen(eventType: KClass<T>): Flow<T>

}


/**
 * Listens as Flow for an event of the given type.
 */
inline fun <reified T : Event> EventScope.listen(): Flow<T> {
    return listen(T::class)
}

internal class EventScopeImpl : EventScope {

    companion object {
        private val logger: Logger = LogManager.getLogger(EventScopeImpl::class.java)
    }

    private val publisher = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    override val coroutineContext: CoroutineContext = Dispatchers.Unconfined

    override fun <T : Event> listen(eventType: KClass<T>): Flow<T> {
        @Suppress("UNCHECKED_CAST")
        return publisher.filter { eventType.isInstance(it) } as Flow<T>
    }

    override fun dispatch(event: Event) {
        if (!publisher.tryEmit(event))
            logger.warn("Failed to emit event: $event")
        else
            logger.debug(event.toString())
    }

}