package me.devnatan.katan.api.server

import me.devnatan.katan.api.EmptySuspendBlock
import me.devnatan.katan.api.process.Process

interface Server {

    val id: Int

    val name: String

    var state: ServerState

    val path: ServerPath

    var query: ServerQuery?

    var process: Process

    var initParams: String

    /**
     * Starts the server and its process asynchronously.
     * When the process is started the [callback] will be called.
     * @throws IllegalStateException = if the server is already running.
     */
    suspend fun start(callback: EmptySuspendBlock)

    /**
     * Shut down the server and stop your process.
     * This method is delayed, it will wait
     * for the process to finish invoking the [callback].
     * @throws IllegalStateException = if the server has not started.
     */
    suspend fun stop(callback: EmptySuspendBlock)

    /**
     * It disconnects the server and its process forcibly,
     * and calls the [callback] at the end of the function.
     */
    suspend fun kill(callback: EmptySuspendBlock)

}