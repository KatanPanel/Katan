package me.devnatan.katan.api.security.auth

import me.devnatan.katan.api.account.Account
import me.devnatan.katan.api.security.credentials.Credentials
import me.devnatan.katan.api.service.ServiceManager

/**
 * Authentication providers are used to guarantee various forms
 * of authentication in addition to the native Katan standard.
 *
 * Plugins must use a different [ExternalAuthenticationProvider],
 * it will be visible at the [ServiceManager] and will be used if requested.
 */
interface AuthenticationProvider {

    /**
     * Attempts to authenticate an [account] with the specified [credentials].
     * @param account the account to be authenticated.
     * @param credentials the credentials that will be used for authentication.
     * @return `true` if the account has been successfully authenticated or` false` otherwise.
     */
    suspend fun authenticate(account: Account, credentials: Credentials): Boolean

}