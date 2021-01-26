package me.devnatan.katan.api

open class KatanException(
    message: String,
    cause: Throwable?
) : RuntimeException(message, cause)