package me.devnatan.katan.core.manager

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import me.devnatan.katan.Katan
import me.devnatan.katan.api.account.KAccount
import me.devnatan.katan.api.account.KUserAccount
import me.devnatan.katan.core.sql.AccountEntity
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class AccountManager(private val core: Katan) {

    private companion object {
        val logger = LoggerFactory.getLogger(AccountManager::class.java)!!
    }

    internal val accounts   = ConcurrentHashMap.newKeySet<KAccount>()!!
    private val algorithm   = Algorithm.HMAC256(core.config.get<String>("auth", "secret"))
    private val verifier    = JWT.require(algorithm).withIssuer("auth0").build()

    /**
     * Returns an existing account in the database with the specified username.
     * @param username account username
     */
    fun getAccount(username: String): KAccount? {
        return accounts.find { it.username == username }
    }

    /**
     * Returns an existing account in the database with the specified id.
     * @param id account id
     */
    fun getAccountById(id: String): KAccount? {
        return accounts.find { it.id.toString() == id }
    }

    /**
     * Create a new account with the specified
     * username and password and add it to the account list.
     * @param username account username
     * @return [KAccount]
     */
    fun createAccount(username: String, password: String): KAccount {
        return KUserAccount(UUID.randomUUID(), username, password).also {
            accounts.add(it)
            logger.debug("Account $username created")
        }
    }

    /**
     * Register an account in the database.
     * @param account the account to register
     */
    fun registerAccount(account: KAccount) = transaction(core.database) {
        AccountEntity.new(account.id) {
            this.username = account.username
            this.password = (account as KUserAccount).password
            this.permissions = 0
        }
    }.also { logger.debug("Account $account registered") }

    /**
     * Checks whether an account exists with the specified username.
     * @param username account username
     */
    fun existsAccount(username: String): Boolean {
        return accounts.any { it.username == username }
    }

    /**
     * Authenticates a username and password if an account for this username exists,
     * the password is verified if correct a token used for identification is returned.
     * @throws IllegalArgumentException if the account does not exist
     * @throws IllegalAccessError if the password is incorrect
     */
    fun auth(username: String, password: String): String {
        val account = getAccount(username) as? KUserAccount ?: throw IllegalArgumentException()
        if (account.password != password)
            throw IllegalAccessError()

        return JWT.create().withIssuer("auth0")
            .withClaim("id", account.id.toString())
            .sign(algorithm).also { logger.debug("Account $username successfully authenticated") }
    }

    /**
     * Checks whether the specified token is valid,
     * if it contains any content and returns the account linked to it.
     * @throws IllegalArgumentException if token is empty or haven't any account linked to it.
     */
    fun verify(token: String): KAccount {
        if (token.isBlank())
            throw IllegalArgumentException("Empty token")

        try {
            val claim = verifier.verify(token).getClaim("id")
            if (claim.isNull)
                throw IllegalArgumentException(token)

            return (getAccountById(claim.asString()) ?: throw IllegalArgumentException("null"))
        } catch (e: JWTDecodeException) {
            throw IllegalArgumentException(e.message)
        }
    }

}