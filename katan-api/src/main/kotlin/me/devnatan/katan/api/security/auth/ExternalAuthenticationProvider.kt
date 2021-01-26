package me.devnatan.katan.api.security.auth

import me.devnatan.katan.api.security.UntrustedProvider
import me.devnatan.katan.api.service.ServiceManager

/**
 * External authentication provider, visible to the [ServiceManager].
 */
interface ExternalAuthenticationProvider : AuthenticationProvider, UntrustedProvider {

    /**
     * Returns the authentication provider id.
     */
    val id: String

}