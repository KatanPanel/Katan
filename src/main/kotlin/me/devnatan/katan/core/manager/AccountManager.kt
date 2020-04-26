package me.devnatan.katan.core.manager

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import me.devnatan.katan.Katan
import me.devnatan.katan.api.account.KAccount
import me.devnatan.katan.api.account.KUserAccount
import me.devnatan.katan.core.sql.AccountEntity
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class AccountManager(private val core: Katan) {

    private val logger      = LoggerFactory.getLogger(AccountManager::class.java)!!
    private val accounts    = ConcurrentHashMap.newKeySet<KAccount>()
    private val algorithm   = Algorithm.HMAC256(core.config.get<String>("auth", "secret"))
    private val verifier    = JWT.require(algorithm).withIssuer("auth0").build()

    /**
     * Returns an existing account in the database with the specified username.
     * @param username account username
     * @return [KAccount] | `null`
     */
    fun getAccount(username: String): KAccount? {
        return accounts.find { it.username == username }
    }

    /**
     * Returns an existing account in the database with the specified id.
     * @param id account id
     * @return [KAccount] | `null`
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
     */
    fun registerAccount(account: KAccount) = transaction(core.database) {
        AccountEntity.new {
            this.username = account.username
            this.password = account.password
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
    @Throws(IllegalArgumentException::class, IllegalAccessError::class)
    fun auth(username: String, password: String): String {
        val account = getAccount(username) ?: throw IllegalArgumentException()
        if (account.password != password)
            throw IllegalAccessError()

        return JWT.create().withIssuer("auth0")
            .withClaim("id", account.id.toString())
            .sign(algorithm).also { logger.debug("Account $username successfully authenticated") }
    }

    /**
     * Checks whether the specified token is valid,
     * if it contains any content and returns the account linked to it.
     * @throws IllegalArgumentException if there is no account linked to it.
     */
    @Throws(IllegalArgumentException::class)
    fun verify(token: String): KAccount {
        val claim = verifier.verify(token).getClaim("id")
        if (claim.isNull)
            throw IllegalArgumentException(token)

        return getAccountById(claim.asString())!!.also { it.password = "<hidden>" }
    }

}