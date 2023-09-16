package org.katan.model

@Serializable
@JvmInline
public value class Snowflake @PublishedApi internal constructor(public val value: Long)

@Suppress("NOTHING_TO_INLINE")
public inline fun Long.toSnowflake(): Snowflake = Snowflake(this)

@Suppress("NOTHING_TO_INLINE")
public inline fun String.toSnowflake(): Snowflake = toLong().toSnowflake()
