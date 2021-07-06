/*
 * Copyright 2020-present Natan Vieira do Nascimento
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.devnatan.katan.api.security.auth

import me.devnatan.katan.api.security.Credentials
import me.devnatan.katan.api.security.account.Account
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
    suspend fun authenticate(
        account: Account,
        credentials: Credentials
    ): Boolean

}