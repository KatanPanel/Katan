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

package me.devnatan.katan.api.util

import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * A delegate that allows a variable to be modified only once, can resemble a [Delegates.vetoable],
 * vetoing the modification if the variable has already been defined, throwing a [IllegalStateException].
 */
class InitOnceProperty<T> : ReadWriteProperty<Any, T> {

    private object EMPTY

    private var value: Any? = EMPTY

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        if (!isInitialized())
            throw UnsupportedOperationException("Property not yet initialized: ${property.name}.")
        else {
            @Suppress("UNCHECKED_CAST")
            return value as T
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        if (isInitialized())
            throw IllegalStateException("Property ${property.name} already initialized.")
        this.value = value
    }

    private fun isInitialized(): Boolean {
        return value !== EMPTY
    }

}