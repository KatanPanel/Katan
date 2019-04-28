package me.devnatan.katan.backend.server

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import me.devnatan.katan.backend.Katan
import me.devnatan.katan.backend.io.KProcess

class KServer(
    val id: String,
    val path: KServerPath,
    @Transient val process: KProcess,
    var state: EnumKServerState
) {

    @Transient var onMessage: ((String) -> Unit)? = null

    fun startAsync(): Deferred<Unit> {
        state = EnumKServerState.STARTING
        return process.coroutine.async {
            process.onMessage = onMessage
            process.startAsync() // block here

            if (process.process!!.isAlive) {
                state = EnumKServerState.RUNNING
                Katan.logger.info("Server [$id] running.")
            } else {
                state = EnumKServerState.STOPPED
                Katan.logger.warn("Failed to start server [$id]: process is not alive.")
            }
        }
    }

    fun stop(force: Boolean = false) {
        if (state == EnumKServerState.STOPPED)
            return

        process.interrupt(force)
        state = EnumKServerState.STOPPED
        Katan.logger.info("Server [$id] stopped.")
    }

    fun write(command: String) {
        process.write(command)
        Katan.logger.info("Writting \"/$command\" to [$id]...")
    }

}

