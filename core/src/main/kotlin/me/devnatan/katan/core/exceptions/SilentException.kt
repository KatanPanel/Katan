package me.devnatan.katan.core.exceptions

class SilentException(cause: Throwable) : RuntimeException(cause)

fun throwSilent(ex: Throwable): Nothing = throw SilentException(ex)