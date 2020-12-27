package me.devnatan.katan.core.util

import com.github.dockerjava.api.async.ResultCallback
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.sendBlocking
import java.io.Closeable
import kotlin.coroutines.cancellation.CancellationException

@OptIn(ExperimentalCoroutinesApi::class)
class FlowResultCallback<T, R>(private val scope: ProducerScope<R>, inline val transform: (T) -> R) : ResultCallback<T> {

    private var lastValue: String? = null

    private var stream: Closeable? = null
    private var closed = false

    override fun onStart(closeable: Closeable) {
        if (closed) return
        stream = closeable
    }

    override fun onNext(value: T) {
        if (closed || stream == null) return
        try {
            scope.sendBlocking(transform(value))
        } catch (e: Throwable) {
            close0()
        }
    }

    override fun onError(cause: Throwable) {
        close0(cause)
    }

    override fun onComplete() {
        close0()
    }

    override fun close() {
        close0()
    }

    private fun close0(cause: Throwable? = null) {
        if (closed) return
        stream?.close()
        scope.cancel(cause?.let { if (it !is CancellationException) CancellationException(it) else it })
        stream = null
        lastValue = null
        closed = true
    }

}

@OptIn(ExperimentalCoroutinesApi::class)
class DeferredResultCallback<T, R>(
    inline val transform: (T) -> R
) : ResultCallback<T>, CompletableDeferred<R> by CompletableDeferred() {

    private var stream: Closeable? = null
    private var closed = false

    override fun onStart(closeable: Closeable) {
        if (closed) return
        stream = closeable
    }

    override fun onNext(value: T) {
        if (closed) return
        try {
            complete(transform(value))
        } catch (e: Throwable) {
            close0()
        }
    }

    override fun onError(cause: Throwable) {
        close0(cause)
    }

    override fun onComplete() {
        close0()
    }

    override fun close() {
        close0()
    }

    private fun close0(cause: Throwable? = null) {
        if (closed) return
        cause?.let {
            completeExceptionally(it)
        }
        stream?.close()
        stream = null
        closed = true
    }

}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T, R> ProducerScope<R>.attachResultCallback(transform: (T) -> R): FlowResultCallback<T, R> {
    return FlowResultCallback(this, transform)
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T, R> deferredResultCallback(transform: (T) -> R): DeferredResultCallback<T, R> {
    return DeferredResultCallback(transform)
}