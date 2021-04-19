package me.devnatan.katan.common.util

import java.io.File
import java.io.InputStream
import java.nio.file.Files

fun exportResource(
    resource: String,
    parent: File? = null,
    classLoader: ClassLoader = Thread.currentThread().contextClassLoader
): File {
    val file = File(parent, resource)
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

fun Long.toFileSizeFormat(): String {
    return when {
        this == Long.MIN_VALUE || this < 0 -> "N/A"
        this < 1024L -> "$this B"
        this <= 0xfffccccccccccccL shr 40 -> "%.1f KiB".format(this.toDouble() / (0x1 shl 10))
        this <= 0xfffccccccccccccL shr 30 -> "%.1f MiB".format(this.toDouble() / (0x1 shl 20))
        this <= 0xfffccccccccccccL shr 20 -> "%.1f GiB".format(this.toDouble() / (0x1 shl 30))
        this <= 0xfffccccccccccccL shr 10 -> "%.1f TiB".format(this.toDouble() / (0x1 shl 40))
        this <= 0xfffccccccccccccL -> "%.1f PiB".format((this shr 10).toDouble() / (0x1 shl 40))
        else -> "%.1f EiB".format((this shr 20).toDouble() / (0x1 shl 40))
    }
}