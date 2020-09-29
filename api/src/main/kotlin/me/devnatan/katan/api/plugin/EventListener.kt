package me.devnatan.katan.api.plugin

import br.com.devsrsouza.eventkt.EventScope
import br.com.devsrsouza.eventkt.scopes.LocalEventScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

/**
 * Responsible for sending events from the plugin and receiving events for the plugin, also known as EventBus.
 */
open class EventListener(private val coroutineScope: CoroutineScope) : EventScope by LocalEventScope() {

    @JvmSynthetic
    @OptIn(ExperimentalStdlibApi::class)
    fun <T : Any> event(event: Class<T>): EventFlow<T> {
        return EventFlow(coroutineScope, listen(event.kotlin))
    }

}

/**
 * A class to help compatibility with Java since it is not possible to operate using `suspending functions` over there.
 * Some [Flow] functions that use this have been implemented here using Java conventions.
 */
class EventFlow<T> internal constructor(
    private val coroutineScope: CoroutineScope,
    private val flow: Flow<T>
) {

    /**
     * Performs the given [action] on each value of the flow.
     * @see Flow.onEach
     */
    fun each(action: Consumer<T>): EventFlow<T> {
        flow.onEach { value ->
            action.accept(value)
        }
        return this
    }

    /**
     * Returns the first element emitted by the [flow] and then cancels flow's collection.
     * @see Flow.first
     */
    fun collect(): T {
        val future = CompletableFuture<T>()
        val job = coroutineScope.async {
            flow.first()
        }

        job.invokeOnCompletion { error ->
            error?.let {
                future.completeExceptionally(error)
            } ?: future.complete(job.getCompleted())
        }

        return future.get()
    }

    /**
     * Returns the first element emitted by the [flow] and then cancels flow's collection.
     * @see Flow.first
     */
    fun collectAsync(): Deferred<T> {
        return coroutineScope.async {
            flow.first()
        }
    }

}