package me.devnatan.katan.api.account

import me.devnatan.katan.api.security.credentials.Credentials
import java.util.*

/**
 * Responsible for [Account] management and authentication.
 */
interface AccountManager {

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

    /**
     * Attempts to authenticate an account with the specified password.
     * @param account the account to be authenticated.
     * @param credentials the credentials that will be used for authentication.
     * @return `true` if the account has been successfully authenticated or` false` otherwise.
     */
    suspend fun authenticateAccount(account: Account, credentials: Credentials): Boolean

}