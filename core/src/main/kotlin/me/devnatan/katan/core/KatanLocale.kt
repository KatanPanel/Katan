package me.devnatan.katan.core

import java.text.MessageFormat
import java.util.*

class KatanLocale(
    val locale: Locale,
    private val messages: Properties,
    private val internal: Properties
) {

    operator fun get(key: String, vararg args: Any): String {
        return if (!messages.containsKey(key))
            "Missing \"$key\" key"
        else MessageFormat(messages.getProperty(key), locale).format(args)
    }

    fun internal(key: String, vararg args: Any): String {
        return if (!internal.containsKey(key))
            "Missing \"$key\" key"
        else MessageFormat(internal.getProperty(key), locale).format(args)
    }

}