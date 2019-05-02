package me.devnatan.katan.backend.server

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import me.devnatan.katan.backend.io.KProcess

class KServer(
    val id: String,
    val path: KServerPath,
    @Transient val process: KProcess,
    var state: EnumKServerState,
    var query: KServerQuery
) {

    @Transient var onMessage: ((String) -> Unit)? = null

    fun startAsync(callback: suspend () -> Unit): Deferred<Unit> {
        state = EnumKServerState.STARTING
        return process.coroutine.async {
            process.onMessage = onMessage
            process.startAsync {
                state = if (process.process!!.isAlive)
                    EnumKServerState.RUNNING
                else
                    EnumKServerState.STOPPED
                callback()
            }
        }
    }

    fun stop(force: Boolean = false, callback: suspend () -> Unit) {
        if (state == EnumKServerState.STOPPED)
            return

        process.interrupt(force) {
            state = EnumKServerState.STOPPED
            callback()
        }
    }

    fun write(command: String) {
        if (!process.isReady())
            return

        process.write(command)
    }

}