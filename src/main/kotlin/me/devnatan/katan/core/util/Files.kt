@file:JvmName("FileUtil")
package me.devnatan.katan.core.util

import me.devnatan.katan.Katan
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.charset.StandardCharsets

fun readResource(resource: String): String {
    val source = Katan::class.java.classLoader.getResource(resource) ?: throw IllegalArgumentException(resource)
    val file = File(source.file)
    if (!file.exists())
        throw FileNotFoundException(resource)

    return String(FileInputStream(file).use { it.readBytes() }, StandardCharsets.UTF_8)
}