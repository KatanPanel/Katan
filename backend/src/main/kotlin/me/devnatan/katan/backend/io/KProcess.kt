package me.devnatan.katan.backend.io

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class KProcess(
    private val builder: ProcessBuilder,
    val coroutine: CoroutineScope
) {

    var process: Process? = null

    private lateinit var reader: BufferedReader
    private lateinit var writer: BufferedWriter

    val output: MutableList<String> = mutableListOf()

    suspend fun startAsync() = coroutine.async {
        withContext(Dispatchers.IO) {
            process = builder.start()
        }

        reader = BufferedReader(InputStreamReader(process!!.inputStream))
        writer = BufferedWriter(OutputStreamWriter(process!!.outputStream))

        readBlocking { line ->
            output.add(line)
        }
    }

    private fun readBlocking(block: (String) -> Unit) {
        var lastLine = reader.readLine()

        while (lastLine != null) {
            block(lastLine)
            lastLine = reader.readLine()
        }
    }

}