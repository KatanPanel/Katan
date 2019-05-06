package me.devnatan.katan.api.process

import me.devnatan.katan.api.EmptySuspendBlock
import me.devnatan.katan.api.SuspendBlock
import java.io.Writer
import java.util.*

interface Process {

    val process: java.lang.Process?

    val builder: ProcessBuilder

    val output: Queue<String>

    val handler: ProcessHandler

    /**
     * Starts the process asynchronously and invokes the [callback].
     * @throws IllegalStateException = if the process is already alive.
     */
    suspend fun start(callback: EmptySuspendBlock)

    /**
     * It waits until the process is finished and invokes [callback].
     * @throws IllegalStateException = if the process is not alive.
     */
    suspend fun stop(callback: EmptySuspendBlock)

    /**
     * Stops the process forcibly and invokes [callback].
     * @throws IllegalStateException = if the process is not alive.
     */
    suspend fun kill(callback: EmptySuspendBlock)

    /**
     * Read the process's [java.io.InputStream] asynchronously.
     * It is an uninterrupted process and waiting for some data.
     * If any data is found it is converted to [java.lang.String] and [callback] is invoked.
     * @throws InstantiationException = if the process is already being read.
     */
    suspend fun read(callback: SuspendBlock<String>)

    /**
     * Write a new [line] (command) in the process writer.
     * After that, the [Writer.flush] method is called.
     * @param line = command to be executed.
     */
    fun write(line: String)

}