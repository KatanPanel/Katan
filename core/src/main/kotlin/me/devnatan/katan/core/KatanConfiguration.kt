package me.devnatan.katan.core

import java.util.AbstractMap

class KatanConfiguration(private val values: Map<*, *>) {

    companion object {

        const val SECTION_DELIMITER = "."

    }

    operator fun <T> get(key: String): T {
        synchronized(values) {
            val entry = split(0, values, key.split(SECTION_DELIMITER), true)
            return entry.value as? T ?: throw NoSuchElementException("Configuration key ${entry.key} not found")
        }
    }

    fun <T> getOrNull(key: String): T? {
        return runCatching { get<T>(key) }.getOrNull() ?: null
    }

    fun <T> getOrDefault(key: String, defaultValue: T): T {
        return runCatching { get<T>(key) }.getOrNull() ?: defaultValue
    }

    fun set(key: String, value: Any) {
        if (values !is MutableMap<*, *>)
            throw UnsupportedOperationException("Immutable values")

        synchronized(values) {
            val entry = split(0, values, key.split(SECTION_DELIMITER), false).also {
                if (it.value == null)
                    throw NoSuchElementException()

                if (it.value !is MutableMap<*, *>)
                    throw UnsupportedOperationException("Not a mutable map")
            }

            (entry.value as MutableMap<String, Any>)[entry.key as String] = value
        }
    }

    fun has(key: String): Boolean {
        synchronized(values) {
            return split(0, values, key.split(SECTION_DELIMITER), true).value != null
        }
    }

    private tailrec fun split(
        idx: Int,
        values: Map<*, *>,
        keys: List<String>,
        transverse: Boolean
    ): Map.Entry<*, *> {
        var index = idx
        val key = keys[index]
        val value = values[key]
        if (transverse && value is Map<*, *> && index < keys.size - 1)
            return split(++index, value, keys, transverse)

        return AbstractMap.SimpleImmutableEntry(key, value)
    }

}