package me.devnatan.katan.api.io

import me.devnatan.katan.api.KatanException

open class FileSystemException protected constructor(
    message: String,
    cause: Throwable? = null
) : KatanException(message, cause)

class UnauthorizedFileSystemAccessException(
    message: String,
    cause: Throwable? = null
) : FileSystemException(message, cause)