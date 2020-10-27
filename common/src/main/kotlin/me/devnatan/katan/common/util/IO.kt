package me.devnatan.katan.common.util

import java.io.File
import java.io.InputStream
import java.nio.file.Files

fun exportResource(resource: String, classLoader: ClassLoader = Thread.currentThread().contextClassLoader): File {
    val file = File(resource)
    if (!file.exists()) {
        // create parent directories
        file.canonicalFile.parentFile.mkdirs()

        loadResource(resource, classLoader).use { input ->
            Files.copy(input, file.toPath())
        }
    }

    return file
}

fun loadResource(resource: String, classLoader: ClassLoader = Thread.currentThread().contextClassLoader): InputStream {
    return classLoader.getResourceAsStream(resource)!!
}