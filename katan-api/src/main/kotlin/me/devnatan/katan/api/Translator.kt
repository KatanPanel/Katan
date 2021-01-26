package me.devnatan.katan.api

import java.text.MessageFormat
import java.util.*

interface Translator {

    /**
     * Returns the current [Locale] of this translator.
     */
    val locale: Locale

    /**
     * Returns the value of the message containing the specified [key] translated into the current [locale].
     * @param key the message key.
     * @param args the message replacement parameters.
     */
    fun translate(key: String, vararg args: Any): String

}

/**
 * A [Translator] who uses as a translation provider a map with the keys and values, the [messages].
 */
open class MapBasedTranslator(override val locale: Locale, private val messages: Map<String, Any>) : Translator {

    override fun translate(key: String, vararg args: Any): String {
        if (!messages.containsKey(key))
            return "Missing $key"

        return MessageFormat(messages.getValue(key).toString(), locale).format(args)
    }

}