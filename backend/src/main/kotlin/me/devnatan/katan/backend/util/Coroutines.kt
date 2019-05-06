package me.devnatan.katan.backend.util

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun CoroutineScope.timer(interval: Long, fixedRate: Boolean = true, action: suspend TimerScope.() -> Unit): Job {
    return launch {
        val scope = TimerScope()

        while (true) {
            val time = measureTimeMillis {
                try {
                    action(scope)
                } catch (ex: Exception) { }
            }

            if (scope.isCanceled) {
                break
            }

            if (fixedRate) {
                delay(Math.max(0, interval - time))
            } else {
                delay(interval)
            }

            yield()
        }
    }
}

class TimerScope {
    var isCanceled: Boolean = false
        private set

    fun cancel() {
        isCanceled = true
    }
}