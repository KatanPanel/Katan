package me.devnatan.katan.backend.impl.server

import com.fasterxml.jackson.annotation.JsonIgnore
import me.devnatan.katan.api.EmptySuspendBlock
import me.devnatan.katan.api.process.Process
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerPath
import me.devnatan.katan.api.server.ServerQuery
import me.devnatan.katan.api.server.ServerState

class ServerImpl(override val id: Int,
                 override val name: String,
                 override val path: ServerPath) : Server {

    override var state: ServerState = ServerState.STOPPED
    override var query: ServerQuery? = null
    @JsonIgnore override lateinit var process: Process
    override lateinit var initParams: String

    override suspend fun start(callback: EmptySuspendBlock) {
        if (state.isRunning)
            throw IllegalStateException("Server already is running")

        process.start {
            state = if (process.process!!.isAlive) ServerState.RUNNING
            else ServerState.STOPPED

            callback()
        }
    }

    override suspend fun stop(callback: EmptySuspendBlock) {
        if (!state.isRunning)
            throw IllegalStateException("Server is not running")

        process.stop {
            state = ServerState.STOPPED
            callback()
        }
    }

    override suspend fun kill(callback: EmptySuspendBlock) {
        if (!state.isRunning)
            throw IllegalStateException("Server is not running")

        process.kill {
            callback()
            state = ServerState.STOPPED
        }
    }

}