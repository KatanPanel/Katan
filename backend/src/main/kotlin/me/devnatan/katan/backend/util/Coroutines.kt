package me.devnatan.katan.backend.util

import kotlinx.coroutines.*
import me.devnatan.katan.api.SuspendBlock
import kotlin.system.measureTimeMillis

/**
 * Starts a new scope-defined Coroutine for [TimerScope].
 * @param interval  = interval in which the [block] will be executed.
 * @param fixedRate = execute action using the time the [block] took to execute.
 * @param block     = action to be performed at each time interval.
 */
fun CoroutineScope.timer(interval: Long, fixedRate: Boolean = true, block: SuspendBlock<TimerScope>): Job {
    return launch {
        val scope = TimerScope()

        while (true) {
            if (fixedRate) {
                val time = measureTimeMillis {
                    block(scope)
                }

                if (scope.isCanceled) break
                delay(Math.max(0, interval - time))
            } else {
                block(scope)

                if (scope.isCanceled) break
                delay(interval)
            }

            yield()
        }
    }
}

class TimerScope {
    var isCanceled: Boolean = false
        private set

    /**
     * Cancels the current timer.
     * @throws IllegalStateException if the timer is not running.
     */
    fun cancel() {
        isCanceled = true
    }

}