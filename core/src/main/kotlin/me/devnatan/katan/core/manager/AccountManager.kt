package me.devnatan.katan.core.manager

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import kotlinx.coroutines.Dispatchers
import me.devnatan.katan.api.account.Account
import me.devnatan.katan.core.Katan
import me.devnatan.katan.core.dao.AccountEntity
import me.devnatan.katan.core.impl.account.AccountImpl
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.NoSuchElementException

class AccountManager(private val core: Katan) {

    private companion object {

        const val JWT_ACCOUNT_ID_FIELD = "id"
        const val JWT_ISSUER = "Katan"
        const val JWT_AUDIENCE = "Katan"
        val JWT_TOKEN_LIFETIME = Duration.ofMinutes(10)!!

        val logger = LoggerFactory.getLogger(AccountManager::class.java)!!

    }

    init {
        logger.info("Loading accounts...")
        transaction(core.database) {
            AccountEntity.all().forEach { entity ->
                val account = AccountImpl(entity.id.value, entity.username, entity.password)
                // TODO: set permissions

                accounts.add(account)
            }
        }
    }

    private val accounts = hashSetOf<Account>()
    private val algorithm = Algorithm.HMAC256(core.config.getString("web.security.authentication.secret"))
    private val verifier = JWT.require(algorithm).withIssuer(JWT_ISSUER).withAudience(JWT_AUDIENCE).build()!!

    /**
     * Returns an existing account in the database with the specified username.
     * @param username account username
     */
    fun getAccount(username: String): Account? {
        return synchronized(accounts) {
            accounts.find { it.username == username }
        }
    }

    /**
     * Returns an existing account in the database with the specified id.
     * @param id account id
     */
    fun getAccount(id: UUID): Account? {
        return synchronized(accounts) {
            accounts.find { it.id == id }
        }
    }

    /**
     * Create a new account with the specified
     * username and password and add it to the account list.
     * @param username account username
     * @throws IllegalArgumentException if account already exists.
     * @return the created account.
     */
    fun createAccount(username: String, password: String): Account {
        val account = AccountImpl(UUID.randomUUID(), username, password)
        return synchronized(accounts) {
            if (!accounts.add(account))
                throw IllegalArgumentException(username)

            account
        }
    }

    /**
     * Register an account in the database.
     * @param account the account to register
     */
    suspend fun registerAccount(account: Account) {
        newSuspendedTransaction(Dispatchers.Default, core.database) {
            AccountEntity.new(account.id) {
                this.username = account.username
                this.password = account.password
                this.permissions = 0
            }
        }
    }

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
     * @throws NoSuchElementException if the account does not exist
     * @throws IllegalArgumentException if the password is incorrect
     * @return a JWT token linked to the account
     */
    fun auth(username: String, password: String): String {
        val account = getAccount(username) ?: throw IllegalArgumentException()
        if (account.password != password)
            throw IllegalArgumentException()

        return JWT.create()
            .withIssuer(JWT_ISSUER)
            .withAudience(JWT_AUDIENCE)
            .withClaim(JWT_ACCOUNT_ID_FIELD, account.id.toString())
            .withExpiresAt(Date.from(Instant.now().plus(JWT_TOKEN_LIFETIME)))
            .sign(algorithm)
    }

    /**
     * Checks whether the specified token is valid,
     * if it contains any content and returns the linked account.
     * * @throws NoSuchElementException if haven't any account linked to it.
     * @throws IllegalArgumentException if token is empty or blank
     * @see com.auth0.jwt.JWTVerifier.verify
     */
    @Throws(JWTDecodeException::class)
    fun verify(token: String): Account {
        if (token.isBlank())
            throw IllegalArgumentException("Empty token")

        val payload = verifier.verify(token)

        val claim = payload.getClaim("id")
        if (claim.isNull)
            throw IllegalArgumentException(token)

        return getAccount(claim.asString()) ?: throw NoSuchElementException()
    }

}