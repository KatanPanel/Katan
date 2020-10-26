package me.devnatan.katan.common.util

import com.typesafe.config.Config

fun <T> Config.get(path: String, defaultValue: T): T {
    return if (hasPath(path))
        getValue(path).unwrapped() as T
    else defaultValue
}

fun <T> Config.get(path: String): T? {
    return getValue(path).unwrapped() as T
}

inline fun <V> Config.getMap(path: String, mapper: (Any?) -> V = { it!! as V }): Map<String, V> {
    if (!hasPath(path))
        return emptyMap()

    return (getValue(path).unwrapped() as Map<String, Any?>).mapValues { mapper(it.value) }
}

fun Config.getStringMap(path: String) = getMap(path) {
    it?.toString()
}.filterValues {
    it != null
} as Map<String, String>