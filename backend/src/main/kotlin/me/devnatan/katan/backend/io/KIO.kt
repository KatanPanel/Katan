package me.devnatan.katan.backend.io

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream

suspend fun readFile(coroutine: CoroutineScope, file: File, block: (ByteArray) -> Unit) {
    coroutine.launch {
        val bytes = withContext(Dispatchers.IO) {
            FileInputStream(file).use {
                it.readBytes()
            }
        }
        block(bytes)
    }
}

fun createProcess(coroutine: CoroutineScope, dir: File, vararg args: String): KProcess {
    if (args.isEmpty())
        throw ArrayIndexOutOfBoundsException("Process args cannot be empty")

    return KProcess(ProcessBuilder(*args).directory(dir), coroutine)
}