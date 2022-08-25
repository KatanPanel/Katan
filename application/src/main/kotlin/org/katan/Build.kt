package org.katan

import java.io.InputStream
import java.util.Properties
import kotlin.reflect.KClass

private val possiblePaths = arrayOf("build.properties")

private fun KClass<*>.findResource(resource: String): InputStream? {
    return java.classLoader.getResourceAsStream(resource)
}

@Suppress("UNCHECKED_CAST")
fun KClass<*>.readBuildFile(): Map<String, String> {
    for (path in possiblePaths) {
        val res = findResource(path) ?: continue

        return res.use {
            Properties().apply {
                load(it)
            }
        }.toMap() as Map<String, String>
    }

    error("Unable to find build properties: ${possiblePaths.joinToString(", ")}")
}