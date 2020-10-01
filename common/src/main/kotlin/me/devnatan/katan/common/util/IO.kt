package me.devnatan.katan.common.util

import java.io.File
import java.nio.file.Files
import kotlin.reflect.KClass

fun KClass<*>.exportResource(
    resource: String,
    destination: String? = null
): File {
    val file = File(destination ?: resource)
    if (!file.exists())
        Files.copy(java.classLoader.getResourceAsStream(file.name)!!, file.toPath())

    return file
}