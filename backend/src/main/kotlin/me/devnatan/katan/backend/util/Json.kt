package me.devnatan.katan.backend.util

import me.devnatan.katan.backend.Katan

fun String.asJsonMap() = Katan.gson.fromJson(this, Map::class.java)

fun Any.asJsonString() = Katan.gson.toJson(this)