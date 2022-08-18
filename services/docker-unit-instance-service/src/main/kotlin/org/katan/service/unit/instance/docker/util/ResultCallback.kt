package org.katan.service.unit.instance.docker.util

import com.github.dockerjava.api.async.ResultCallback
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.trySendBlocking
import java.io.Closeable
import kotlin.coroutines.cancellation.CancellationException

internal abstract class AbstractResultCallback<T> : ResultCallback<T> {

    private var stream: Closeable? = null

    override fun onStart(closeable: Closeable?) {
        stream?.close()
        stream = closeable
    }

    override fun onError(cause: Throwable) {
        close0(cause)
    }

    override fun onNext(value: T) {
        try {
            handle(value)
        } catch (e: Throwable) {
            close0(e)
        }
    }

    override fun onComplete() {
        close0()
    }

    override fun close() {
        close0()
    }

    private fun close0(cause: Throwable? = null) {
        if (isClosed()) return
        finalize(cause)
        stream?.close()
        stream = null
    }

    private fun isClosed(): Boolean {
        return stream == null
    }

    abstract fun handle(value: T)

    abstract fun finalize(error: Throwable?)
}

private class FlowResultCallback<T, R>(
    private val scope: ProducerScope<R>,
    inline val transform: (T) -> R
) : AbstractResultCallback<T>() {

    override fun handle(value: T) {
        scope.trySendBlocking(transform(value))
    }

    override fun finalize(error: Throwable?) {
        error?.printStackTrace()

        scope.cancel(
            error?.let { cause ->
                if (cause !is CancellationException) {
                    CancellationException(cause)
                } else cause
            }
        )
    }
}

internal class DeferredResultCallback<T, R>(
    inline val transform: (T) -> R
) : AbstractResultCallback<T>(), CompletableDeferred<R> by CompletableDeferred() {

    override fun handle(value: T) {
        complete(transform(value))
    }

    override fun finalize(error: Throwable?) {
        error?.let { cause -> completeExceptionally(cause) }
    }
}

internal fun <T, R> ProducerScope<R>.attachResultCallback(
    transform: (T) -> R
): AbstractResultCallback<T> {
    return FlowResultCallback(this, transform)
}

internal fun <T, R> deferredResultCallback(transform: (T) -> R): AbstractResultCallback<T> {
    return DeferredResultCallback(transform)
}
