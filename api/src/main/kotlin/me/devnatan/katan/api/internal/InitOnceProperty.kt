package me.devnatan.katan.api.internal

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
            throw UnsupportedOperationException("Not yet initialized: ${property.name}")
        else {
            @Suppress("UNCHECKED_CAST")
            return value as T
        }
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        if (isInitialized())
            throw IllegalStateException("Property ${property.name} already initialized")
        this.value = value
    }

    private fun isInitialized(): Boolean {
        return value !== EMPTY
    }

}