@file:JvmName("JsonUtil")
package me.devnatan.katan.backend.util

import me.devnatan.katan.backend.katan

fun String.asJsonMap(): Map<*, *>? = katan.json.readValue(this, Map::class.java)
fun Any.asJsonString(): String? = katan.json.writeValueAsString(this)