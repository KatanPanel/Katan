package org.katan

public open class KatanException(message: String? = null, cause: Throwable? = null) :
    RuntimeException(message, cause)
