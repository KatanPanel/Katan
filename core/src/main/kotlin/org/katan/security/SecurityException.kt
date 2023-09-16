package org.katan.security

import org.katan.KatanException

open class SecurityException(
    message: String? = null,
    cause: Throwable? = null,
) : KatanException(message, cause)

open class AuthenticationException(
    message: String?,
    cause: Throwable? = null,
) : SecurityException(message, cause)

class InvalidAccessTokenException : SecurityException()

class InvalidCredentialsException : SecurityException()
