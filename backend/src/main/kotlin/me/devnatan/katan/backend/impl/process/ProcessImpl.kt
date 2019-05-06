package me.devnatan.katan.backend.impl.process

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.devnatan.katan.api.EmptySuspendBlock
import me.devnatan.katan.api.SuspendBlock
import me.devnatan.katan.api.process.Process
import me.devnatan.katan.api.process.ProcessHandler
import me.devnatan.katan.api.writeln
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class ProcessImpl(override val builder: ProcessBuilder) : Process {

    override var process: java.lang.Process? = null
    override val output: Queue<String> = ConcurrentLinkedQueue()
    override val handler: ProcessHandler = ProcessHandler()

    // IO
    private lateinit var reader: BufferedReader
    private lateinit var writer: BufferedWriter
    private var isReading: Boolean = false

    override suspend fun start(callback: EmptySuspendBlock) {
        if ((process != null) && process!!.isAlive)
            throw IllegalStateException("Process already is alive")

        output.clear()
        withContext(Dispatchers.IO) {
            process = builder.start()
        }

        reader = BufferedReader(InputStreamReader(process!!.inputStream))
        writer = BufferedWriter(OutputStreamWriter(process!!.outputStream))
        handler.onStart?.invoke()

        read {
            output.add(it)
            handler.onMessage?.invoke(it)
        }
    }

    override suspend fun stop(callback: EmptySuspendBlock) {
        if ((process == null) || !process!!.isAlive)
            throw IllegalStateException("Process isn't alive")

        withContext(Dispatchers.IO) {
            write("stop")
            process!!.waitFor()
        }

        callback()
    }

    override suspend fun kill(callback: EmptySuspendBlock) {
        if ((process == null) || !process!!.isAlive)
            throw IllegalStateException("Process isn't alive")

        withContext(Dispatchers.IO) {
            process!!.destroy()
        }

        callback()
    }

    override suspend fun read(callback: SuspendBlock<String>) {
        if (isReading)
            throw InstantiationException("Process is already being read")

        isReading = true
        withContext(Dispatchers.IO) {
            var line = reader.readLine()

            while (line != null) {
                callback(line)
                line = reader.readLine()
            }
        }
    }

    override fun write(line: String) {
        writer.writeln(line)
        writer.flush()
    }

}