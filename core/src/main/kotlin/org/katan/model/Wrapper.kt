package org.katan.model

import kotlinx.serialization.Serializable
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

public interface Wrapper<T> : ReadOnlyProperty<T?, T?> {

    public val value: T?

    override fun getValue(thisRef: T?, property: KProperty<*>): T? = value
}

@Serializable
private data class GenericWrapper<T>(override val value: T?) : Wrapper<T>

@JvmInline
private value class StringWrapper(override val value: String?) : Wrapper<String>

@JvmInline
private value class LongWrapper(override val value: Long?) : Wrapper<Long>

public fun <T> T?.wrap(): Wrapper<T> = GenericWrapper(this)

public fun String?.wrap(): Wrapper<String> = StringWrapper(this)

public fun Long?.wrap(): Wrapper<Long> = LongWrapper(this)

@Suppress("NOTHING_TO_INLINE")
public inline fun <T> Wrapper<T>?.unwrap(): T? = this?.let { value }

public inline fun <T> Wrapper<T>?.ifNotEmpty(block: (T) -> Unit): Unit? = this?.let { block(it.value!!) }
