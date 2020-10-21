package me.devnatan.katan.common.exceptions

import org.slf4j.Logger
import org.slf4j.LoggerFactory

private val somewhere: Logger = LoggerFactory.getLogger("Unknown")

class SilentException(cause: Throwable, val logger: Logger, val exit: Boolean) : RuntimeException(cause)

fun Throwable.silent(logger: Logger = somewhere, exit: Boolean = false): SilentException {
    return SilentException(this, logger, exit)
}

fun throwSilent(exception: Throwable, logger: Logger = somewhere, exit: Boolean = false): Nothing {
    throw exception.silent(logger, exit)
}