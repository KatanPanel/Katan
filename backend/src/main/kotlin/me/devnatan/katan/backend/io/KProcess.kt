package me.devnatan.katan.backend.io

import kotlinx.coroutines.*
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

    private lateinit var reader: BufferedReader
    private lateinit var writer: BufferedWriter
    val output = ConcurrentLinkedQueue<String>() // fast "add"()

    suspend fun startAsync() {
        output.clear()

        withContext(Dispatchers.IO) {
            process = builder.start()
            reader = BufferedReader(InputStreamReader(process!!.inputStream))
            writer = BufferedWriter(OutputStreamWriter(process!!.outputStream))
        }

        coroutine.launch {
            readBlocking { line ->
                output.add(line)
            }
        }
    }

    fun interrupt(force: Boolean = false) {
        if (process == null)
            throw IllegalStateException("Process is not defined yet")

        if (!process!!.isAlive)
            throw IllegalStateException("Process is not alive")


        if (!force) {
            coroutine.async {
                process!!.waitFor()
            }

            process!!.destroy()
        } else process!!.destroyForcibly()
    }

    private fun readBlocking(block: (String) -> Unit) {
        reader.read()
        var lastLine = reader.readLine()

        while (lastLine != null) {
            block(lastLine)
            lastLine = reader.readLine()
        }
    }

}