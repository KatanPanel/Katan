package me.devnatan.katan.cli

class KatanCLIException(
    translationKey: String,
    cause: Throwable? = null,
    vararg val translationArgs: Any
) : Exception(translationKey, cause)