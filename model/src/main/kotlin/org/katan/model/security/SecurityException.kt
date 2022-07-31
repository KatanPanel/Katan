package org.katan.model.security

import org.katan.model.KatanException

open class SecurityException(
    message: String? = null,
    cause: Throwable? = null
) : KatanException(message, cause)

open class AuthenticationException(
    message: String? = null,
    cause: Throwable? = null
) : SecurityException(message, cause)

class InvalidCredentialsException : SecurityException(null, null)
