package me.devnatan.katan.api.security

import me.devnatan.katan.api.KatanException

/**
 * Security exceptions are related to the attempt to access untrusted
 * entities (like [UntrustedProvider]) to protected areas without proper
 * authorization.
 *
 * @author Natan Vieira
 * @since  1.0
 */
open class SecurityException(
    message: String? = null,
    cause: Throwable? = null
) : KatanException(message, cause)