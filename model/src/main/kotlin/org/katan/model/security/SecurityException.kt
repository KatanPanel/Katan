package org.katan.model.security

import org.katan.model.KatanException

public open class SecurityException(
    message: String? = null,
    cause: Throwable? = null,
) : KatanException(message, cause)

public open class AuthenticationException(
    message: String?,
    cause: Throwable? = null,
) : SecurityException(message, cause)

public class InvalidAccessTokenException : SecurityException()

public class InvalidCredentialsException : SecurityException()
