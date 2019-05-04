package me.devnatan.katan.backend.io

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream

suspend fun readFileAsync(coroutine: CoroutineScope, file: File, block: (ByteArray) -> Unit) {
    coroutine.launch {
        val b = withContext(Dispatchers.IO) {
            FileInputStream(file).use {
                it.readBytes()
            }
        }
        block(b)
    }
}

fun readFile(file: File): ByteArray {
    return FileInputStream(file).use { it.readBytes() }
}

fun createProcess(coroutine: CoroutineScope, dir: File, vararg args: String): KProcess {
    if (args.isEmpty())
        throw ArrayIndexOutOfBoundsException("Process args cannot be empty")

    return KProcess(ProcessBuilder(*args).directory(dir), coroutine)
}