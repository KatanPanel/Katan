@file:JvmName("IOUtil")
package me.devnatan.katan.backend.util

import me.devnatan.katan.api.process.Process
import me.devnatan.katan.backend.impl.process.ProcessImpl
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/**
 * Read the contents of the file.
 */
fun File.readStream(): ByteArray = FileInputStream(this).readStream()

/**
 * Read the contents of the InputStream.
 */
fun InputStream.readStream(): ByteArray = use {
    it.readBytes()
}

/**
 * Creates a new process using the [args] startup arguments.
 * @param directory = target directory
 * @param args      = startup parameters.
 */
fun createProcess(directory: File, args: String): Process {
    return ProcessImpl(ProcessBuilder(args.split(" ")).directory(directory))
}