package org.katan.event

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.last
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

interface EventScope : CoroutineScope {

    /**
     * Dispatches an event.
     */
    fun dispatch(event: Any)

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
internal class EventScopeImpl : EventScope {

    companion object {
        private val logger: Logger = LogManager.getLogger(EventScopeImpl::class.java)
    }

    private val publisher = MutableSharedFlow<Any>(extraBufferCapacity = 1)
    override val coroutineContext: CoroutineContext = Dispatchers.Unconfined

    override fun <T : Any> listen(eventType: KClass<T>): Flow<T> {
        @Suppress("UNCHECKED_CAST")
        return publisher.filter { eventType.isInstance(it) } as Flow<T>
    }

    override fun dispatch(event: Any) {
        println("dispatching: $event")

        if (!publisher.tryEmit(event))
            logger.warn("Failed to emit event: $event")
        else
            logger.debug(event.toString())
    }

}