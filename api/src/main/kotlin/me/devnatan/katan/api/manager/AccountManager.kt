package me.devnatan.katan.api.manager

import me.devnatan.katan.api.account.Account
import java.util.*

interface AccountManager {

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

    /**
     * Authenticates a username and password if an account for this username exists,
     * the password is verified if correct a token used for identification is returned.
     * @throws NoSuchElementException if the account does not exist
     * @throws IllegalArgumentException if the password is incorrect
     * @return a token linked to the account
     */
    suspend fun authenticateAccount(username: String, password: String): String

    /**
     * Checks whether the specified token is valid,
     * if it contains any content and returns the linked account.
     * * @throws NoSuchElementException if haven't any account linked to it.
     * @throws IllegalArgumentException if token is empty or blank
     */
    suspend fun verifyToken(token: String): Account

}