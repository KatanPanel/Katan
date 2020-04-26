@file:JvmName("JsonUtil")
package me.devnatan.katan.core.util

import com.fasterxml.jackson.databind.ObjectMapper

/**
 * Converts a Javascript Object Notation (JSON) to a Map.
 */
fun String.fromString(mapper: ObjectMapper): Map<*, *>? = mapper.readValue(this, Map::class.java)

/**
 * Converts an Object to a plain Javascript Object Notation (JSON).
 */
fun Any.toString(mapper: ObjectMapper): String? = mapper.writeValueAsString(this)