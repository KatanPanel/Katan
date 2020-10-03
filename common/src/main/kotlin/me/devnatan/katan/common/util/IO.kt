package me.devnatan.katan.common.util

import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

fun createDirectory(dir: String): File {
    val file = File(dir)
    if (!file.exists())
        Files.createDirectory(Paths.get(dir))

    return file
}

fun exportResource(
    resource: String,
    classLoader: ClassLoader = Thread.currentThread().contextClassLoader,
): File {
    val file = File(resource)
    if (!file.exists())
        Files.copy(classLoader.getResourceAsStream(resource)!!, file.toPath())

    return file
}

fun loadResource(
    resource: String,
    classLoader: ClassLoader = Thread.currentThread().contextClassLoader,
): InputStream {
    return classLoader.getResourceAsStream(resource)!!
}