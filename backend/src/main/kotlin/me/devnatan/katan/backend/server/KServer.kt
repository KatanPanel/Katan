package me.devnatan.katan.backend.server

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import me.devnatan.katan.backend.io.KProcess

class KServer(
    val id: Int,
    val name: String,
    val path: KServerPath,
    @Transient @JsonIgnore val process: KProcess,
    var state: EnumKServerState,
    var query: KServerQuery,
    var initParams: String
) {

    @Transient @JsonIgnore var onMessage: ((String) -> Unit)? = null

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