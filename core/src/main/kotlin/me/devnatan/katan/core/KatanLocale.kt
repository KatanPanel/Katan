package me.devnatan.katan.core

import java.text.MessageFormat
import java.util.*

class KatanLocale(val locale: Locale, private val messages: Properties) {

    operator fun get(key: String, vararg args: Any): String {
        return if (!messages.containsKey(key))
            "<missing locale key: \"$key\">"
        else MessageFormat(messages.getProperty(key), locale).format(args)
    }

}