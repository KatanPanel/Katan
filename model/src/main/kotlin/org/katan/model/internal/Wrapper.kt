package org.katan.model.internal

import kotlinx.serialization.Serializable
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface Wrapper<T> : ReadOnlyProperty<T?, T?> {
    val value: T?

    override fun getValue(thisRef: T?, property: KProperty<*>): T? {
        return value
    }
}

@Serializable
private data class GenericWrapper<T>(override val value: T?) : Wrapper<T>

@JvmInline
private value class StringWrapper(override val value: String?) : Wrapper<String>

@JvmInline
private value class LongWrapper(override val value: Long?) : Wrapper<Long>

fun <T> T?.wrap(): Wrapper<T> = GenericWrapper(this)

fun String?.wrap(): Wrapper<String> = StringWrapper(this)

fun Long?.wrap(): Wrapper<Long> = LongWrapper(this)

@Suppress("NOTHING_TO_INLINE")
inline fun <T> Wrapper<T>?.unwrap(): T? {
    return this?.let { value }
}

inline fun <T> Wrapper<T>?.ifNotEmpty(block: (T) -> Unit) {
    this?.let { block(it.value!!) }
}