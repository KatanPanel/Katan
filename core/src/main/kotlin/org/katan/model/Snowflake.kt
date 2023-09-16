package org.katan.model

import kotlinx.serialization.Serializable

@Serializable
@JvmInline
value class Snowflake(val value: Long) {

    override fun toString(): String = value.toString()
}

@Suppress("NOTHING_TO_INLINE")
inline fun Long.toSnowflake(): Snowflake = Snowflake(this)

@Suppress("NOTHING_TO_INLINE")
inline fun String.toSnowflake(): Snowflake = toLong().toSnowflake()
