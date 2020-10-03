package me.devnatan.katan.core

import java.text.MessageFormat
import java.util.*

class KatanLocale(
    val locale: Locale,
    private val messages: Properties,
    private val internal: Properties
) {

    operator fun get(key: String, vararg args: Any): String {
        return MessageFormat.format(messages.getProperty(key), *args)
    }

    fun internal(key: String, vararg args: Any): String {
        return MessageFormat.format(internal.getProperty(key), *args)
    }

}