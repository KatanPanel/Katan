package me.devnatan.katan.core

import com.typesafe.config.Config

fun <T> Config.get(path: String, defaultValue: T): T {
    return if (hasPath(path))
        getValue(path).unwrapped() as T
    else defaultValue
}

fun <T> Config.get(path: String): T? {
    return getValue(path).unwrapped() as T
}