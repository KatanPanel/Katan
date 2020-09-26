package me.devnatan.katan.core.exceptions

import org.slf4j.Logger

class SilentException(cause: Throwable, val logger: Logger) : RuntimeException(cause)

fun Throwable.asSilent(logger: Logger): SilentException {
    return SilentException(this, logger)
}

fun throwSilent(exception: Throwable, logger: Logger): Nothing {
    throw exception.asSilent(logger)
}