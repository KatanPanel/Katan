package org.katan

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.reflect.KClass

public interface EventsDispatcher : CoroutineScope {

    /**
     * Dispatches an event.
     *
     * @param event The event to be dispatched.
     */
    public fun dispatch(event: Any)

    /**
     * Listens as Flow for an event of the given type.
     *
     * @param eventType Type of the event to listen to.
     */
    public fun <T : Any> listen(eventType: KClass<T>): Flow<T>
}

/**
 * Listens as Flow for an event of the given type.
 */
public inline fun <reified T : Any> EventsDispatcher.listen(): Flow<T> {
    return listen(T::class)
}

internal class EventsDispatcherImpl :
    EventsDispatcher,
    CoroutineScope by CoroutineScope(Dispatchers.IO + SupervisorJob()) {

    companion object {

        private val logger: Logger = LogManager.getLogger(EventsDispatcherImpl::class.java)
    }

    private val publisher = MutableSharedFlow<Any>(extraBufferCapacity = 1)

    override fun <T : Any> listen(eventType: KClass<T>): Flow<T> {
        @Suppress("UNCHECKED_CAST")
        return publisher.filter { event -> eventType.isInstance(event) } as Flow<T>
    }

    override fun dispatch(event: Any) {
        publisher.tryEmit(event)
        logger.trace(event.toString())
    }
}
