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

package me.devnatan.katan.api.security.account

import me.devnatan.katan.api.security.auth.AuthenticationProvider
import java.util.*

/**
 * Responsible for [Account] management and authentication.
 */
interface AccountManager : AuthenticationProvider {

    /**
     * Returns a copy of all registered accounts.
     */
    fun getAccounts(): List<Account>

    /**
     * Returns an existing account in the database with the specified username.
     * @param username the account username.
     */
    suspend fun getAccount(username: String): Account?

    /**
     * Returns an existing account in the database with the specified id.
     * @param id the account id.
     */
    suspend fun getAccount(id: UUID): Account?

    /**
     * Create a new account with the specified username and password and add it to the account list.
     * @param username account username.
     * @throws IllegalArgumentException if account already exists.
     * @return the created account.
     */
    fun createAccount(username: String, password: String): Account

    /**
     * Register an account in the database.
     * @param account the account to register.
     */
    suspend fun registerAccount(account: Account)

    /**
     * Checks whether an account exists with the specified username.
     * @param username the account username.
     */
    fun existsAccount(username: String): Boolean

}