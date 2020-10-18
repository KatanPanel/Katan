package me.devnatan.katan.common.exceptions

import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val somewhere: Logger = LoggerFactory.getLogger("Unknown")

class SilentException(cause: Throwable, val logger: Logger) : RuntimeException(cause)

fun Throwable.silent(logger: Logger = somewhere): SilentException {
    return SilentException(this, logger)
}

fun throwSilent(exception: Throwable, logger: Logger = somewhere): Nothing {
    throw exception.silent(logger)
}