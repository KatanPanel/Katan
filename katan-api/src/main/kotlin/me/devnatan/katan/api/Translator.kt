/*
 * Copyright 2020-present Natan Vieira do Nascimento
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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