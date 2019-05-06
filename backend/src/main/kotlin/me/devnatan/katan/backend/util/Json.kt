@file:JvmName("JsonUtil")
package me.devnatan.katan.backend.util

import me.devnatan.katan.backend.katan

/**
 * Converts a Javascript Object Notation (JSON) to a Map.
 */
fun String.asJsonMap(): Map<*, *>? = katan.json.readValue(this, Map::class.java)

/**
 * Converts an Object to a plain Javascript Object Notation (JSON).
 */
fun Any.asJsonString(): String? = katan.json.writeValueAsString(this)