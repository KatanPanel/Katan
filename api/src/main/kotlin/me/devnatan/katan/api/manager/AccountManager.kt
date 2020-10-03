package me.devnatan.katan.api.manager

import me.devnatan.katan.api.account.Account
import java.util.*

interface AccountManager {

    /**
     * Returns a copy of all registered accounts.
     */
    fun getAccounts(): List<Account>

    /**
     * Returns an existing account in the database with the specified username.
     * @param username account username
     */
    suspend fun getAccount(username: String): Account?

    /**
     * Returns an existing account in the database with the specified id.
     * @param id account id
     */
    suspend fun getAccount(id: UUID): Account?

    /**
     * Create a new account with the specified
     * username and password and add it to the account list.
     * @param username account username
     * @throws IllegalArgumentException if account already exists.
     * @return the created account.
     */
    fun createAccount(username: String, password: String): Account

    /**
     * Register an account in the database.
     * @param account the account to register
     */
    suspend fun registerAccount(account: Account)

    /**
     * Checks whether an account exists with the specified username.
     * @param username account username
     */
    fun existsAccount(username: String): Boolean

    suspend fun authenticateAccount(account: Account, password: String): Boolean

}