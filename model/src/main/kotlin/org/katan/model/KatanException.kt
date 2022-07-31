package org.katan.model

open class KatanException(message: String? = null, cause: Throwable? = null) :
    RuntimeException(message, cause)
