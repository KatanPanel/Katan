@file:JvmName("JsonUtil")
package me.devnatan.katan.backend.util

import me.devnatan.katan.backend.katan

fun String.asJsonMap(): Map<*, *>? = katan.gson.fromJson(this, Map::class.java)
fun Any.asJsonString() = katan.gson.toJson(this)!!