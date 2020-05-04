package me.devnatan.katan

class KatanConfig(private val config: Map<*, *>) {

    operator fun <T> get(vararg keys: String): T {
        return split(0, config, keys) as T
    }

    private tailrec fun split(
        idx: Int,
        values: Map<*, *>,
        keys: Array<out String>
    ): Any {
        var index = idx
        val key = keys[index]
        val value = values[key]
        if (value is Map<*, *> && index < keys.size - 1)
            return split(++index, value, keys)

        return value ?: throw NoSuchElementException(key)
    }

}