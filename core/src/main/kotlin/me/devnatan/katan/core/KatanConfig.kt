package me.devnatan.katan.core

import com.typesafe.config.Config

fun Config.getString(path: String, defaultValue: String?) = runCatching {
    getString(path)
}.getOrNull() ?: defaultValue

fun Config.getBoolean(path: String, defaultValue: Boolean) = runCatching {
    getBoolean(path)
}.getOrNull() ?: defaultValue