package me.devnatan.katan.backend.io

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.concurrent.ConcurrentLinkedQueue

class KProcess(
    private val builder: ProcessBuilder,
    val coroutine: CoroutineScope
) {

    var process: Process? = null
    val output = ConcurrentLinkedQueue<String>() // fast "add"()
    var onMessage: ((String) -> Unit)? = null

    private lateinit var reader: BufferedReader
    private lateinit var writer: BufferedWriter

    fun isReady(): Boolean {
        return process != null && process!!.isAlive
    }

    suspend fun startAsync(callback: suspend () -> Unit) {
        output.clear()

        withContext(Dispatchers.IO) {
            process = builder.start()
            reader = BufferedReader(InputStreamReader(process!!.inputStream))
            writer = BufferedWriter(OutputStreamWriter(process!!.outputStream))
        }

        coroutine.launch {
            callback()
            read { line ->
                output.add(line)
                onMessage?.invoke(line)
            }
        }
    }

    fun interrupt(force: Boolean = false, callback: suspend () -> Unit) {
        if (process == null)
            throw IllegalStateException("Process is not defined yet")

        if (!process!!.isAlive)
            throw IllegalStateException("Process is not alive")

        coroutine.launch {
            withContext(Dispatchers.IO) {
                if (!force) {
                    write("stop")
                    process!!.waitFor()
                } else
                    process!!.destroy()
            }

            callback()
        }
    }

    private fun read(block: (String) -> Unit) {
        var lastLine = reader.readLine()

        while (lastLine != null) {
            block(lastLine)
            lastLine = reader.readLine()
        }
    }

    fun write(command: String) {
        writer.write(command + "\n")
        writer.flush()
    }

}