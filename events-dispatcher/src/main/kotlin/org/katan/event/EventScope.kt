package org.katan.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.reflect.KClass

interface EventScope : CoroutineScope {

    /**
     * Dispatches an event.
     */
    fun dispatch(
        event: Any
    )

    /**
     * Listens as Flow for an event of the given type.
     */
    fun <T : Any> listen(eventType: KClass<T>): Flow<T>
}

/**
 * Listens as Flow for an event of the given type.
 */
inline fun <reified T : Any> EventScope.listen(): Flow<T> {
    return listen(T::class)
}

/** Basic EventScope implementation **/
internal class EventScopeImpl : EventScope, CoroutineScope by CoroutineScope(Dispatchers.IO + SupervisorJob()) {

    companion object {
        private val logger: Logger = LogManager.getLogger(EventScopeImpl::class.java)
    }

    private val publisher = MutableSharedFlow<Any>(extraBufferCapacity = 1)

    override fun <T : Any> listen(eventType: KClass<T>): Flow<T> {
        @Suppress("UNCHECKED_CAST")
        return publisher.filter { eventType.isInstance(it) } as Flow<T>
    }

    override fun dispatch(event: Any) {
        if (!publisher.tryEmit(event)) {
            logger.warn("Failed to emit event: $event")
        } else {
            logger.trace(event.toString())
        }
    }
}
