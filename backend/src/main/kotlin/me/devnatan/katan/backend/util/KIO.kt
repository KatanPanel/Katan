@file:JvmName("IOUtil")
package me.devnatan.katan.backend.util

import me.devnatan.katan.api.process.Process
import me.devnatan.katan.backend.impl.process.ProcessImpl
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

fun File.readStream() = FileInputStream(this).readStream()

fun InputStream.readStream() = use { it.readBytes() }

fun createProcess(directory: File, vararg args: String): Process {
    return ProcessImpl(ProcessBuilder(*args).directory(directory))
}