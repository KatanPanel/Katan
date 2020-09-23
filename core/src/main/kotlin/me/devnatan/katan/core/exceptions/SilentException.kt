package me.devnatan.katan.core.exceptions

class SilentException(cause: Throwable) : RuntimeException(cause)

fun Throwable.silent() = SilentException(this)