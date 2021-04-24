@file:Suppress("UNCHECKED_CAST")

package me.devnatan.katan.common.util

import com.typesafe.config.Config

fun Config.getEnv(path: String, envKey: String): String? {
    return System.getenv(envKey) ?: get<Any>(path)?.toString()
}

fun Config.getEnv(path: String, envKey: String, defaultValue: Any): String {
    return System.getenv(envKey) ?: get(path, defaultValue).toString()
}

inline fun Config.getEnv(path: String, envKey: String, missingSupplier: () -> Nothing): String {
    return getEnv(path, envKey) ?: run {
        missingSupplier()
    }
}

fun Config.getEnvInt(path: String, envKey: String): Int? {
    return getEnv(path, envKey)?.toIntOrNull()
}

fun Config.getEnvInt(path: String, envKey: String, defaultValue: Int): Int {
    return getEnv(path, envKey, defaultValue).toInt()
}

fun Config.getEnvBoolean(path: String, envKey: String, defaultValue: Boolean):
        Boolean {
    return getEnv(path, envKey, defaultValue).toBoolean()
}

fun <T> Config.get(path: String): T? {
    return if (hasPath(path))
        getValue(path).unwrapped() as T
    else null
}

fun <T> Config.get(path: String, defaultValue: T): T {
    return if (hasPath(path))
        getValue(path).unwrapped() as T
    else defaultValue
}

inline fun <V> Config.getMap(
    path: String,
    mapper: (Any?) -> V = { it!! as V }
): Map<String, V> {
    if (!hasPath(path))
        return emptyMap()

    return (getValue(path).unwrapped() as Map<String, Any?>).mapValues {
        mapper(
            it.value
        )
    }
}

fun Config.getStringMap(path: String) = getMap(path) {
    it?.toString()
}.filterValues {
    it != null
} as Map<String, String>