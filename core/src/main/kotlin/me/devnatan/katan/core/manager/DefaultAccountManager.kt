package me.devnatan.katan.core.manager

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import kotlinx.coroutines.Dispatchers
import me.devnatan.katan.api.account.Account
import me.devnatan.katan.api.manager.AccountManager
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.account.AccountImpl
import me.devnatan.katan.core.database.jdbc.JDBCConnector
import me.devnatan.katan.core.database.jdbc.entity.AccountEntity
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.NoSuchElementException

class DefaultAccountManager(private val core: KatanCore) : AccountManager {

    private companion object {

        const val AUTH_SECRET_MIN_LENGTH = 8
        const val AUTH_SECRET_MAX_LENGTH = 32

        private const val JWT_AUDIENCE = "Katan-AccountManager"
        private val JWT_TOKEN_LIFETIME = Duration.ofMinutes(10)!!

        val logger = LoggerFactory.getLogger(AccountManager::class.java)!!

    }

    private val accounts = hashSetOf<Account>()
    private val algorithm: Algorithm
    private val verifier: JWTVerifier

    init {
        val secret = core.config.getString("security.crypto.auth.secret")
        val len = secret.length
        check(len >= AUTH_SECRET_MIN_LENGTH) {
            "Authentication secret must have at least %d characters (given: %d).".format(
                AUTH_SECRET_MIN_LENGTH, len
            )
        }
        check(len <= AUTH_SECRET_MAX_LENGTH) {
            "Authentication secret cannot exceed %d characters (given: %d).".format(
                AUTH_SECRET_MAX_LENGTH, len
            )
        }

        algorithm = Algorithm.HMAC256(secret)
        verifier = JWT.require(algorithm).withAudience(JWT_AUDIENCE).build()!!

        logger.info("Loading accounts...")

        // TODO: create accounts repository
        transaction((core.database as JDBCConnector).database) {
            AccountEntity.all().forEach { entity ->
                val account = AccountImpl(entity.id.value, entity.username, entity.password)
                // TODO: set permissions

                accounts.add(account)
            }
        }
    }

    override fun getAccounts(): List<Account> {
        return accounts.toList()
    }

    override suspend fun getAccount(username: String): Account? {
        return synchronized(accounts) {
            accounts.find { it.username == username }
        }
    }

    override suspend fun getAccount(id: UUID): Account? {
        return synchronized(accounts) {
            accounts.find { it.id == id }
        }
    }

    override fun createAccount(username: String, password: String): Account {
        val account = AccountImpl(UUID.randomUUID(), username, password)
        return synchronized(accounts) {
            if (!accounts.add(account))
                throw IllegalArgumentException(username)

            account
        }
    }

    override suspend fun registerAccount(account: Account) {
        newSuspendedTransaction(Dispatchers.Default, (core.database as JDBCConnector).database) {
            AccountEntity.new(account.id) {
                this.username = account.username
                this.password = account.password
                this.permissions = 0
            }
        }
    }

    override fun existsAccount(username: String): Boolean {
        return accounts.any { it.username == username }
    }

    override suspend fun authenticateAccount(username: String, password: String): String {
        val account = getAccount(username) ?: throw IllegalArgumentException()
        if (account.password != password)
            throw IllegalArgumentException()

        return JWT.create()
            .withAudience(JWT_AUDIENCE)
            .withClaim("account", account.id.toString())
            .withExpiresAt(Date.from(Instant.now().plus(JWT_TOKEN_LIFETIME)))
            .sign(algorithm)
    }

    /**
     * @see com.auth0.jwt.JWTVerifier.verify
     */
    @Throws(JWTDecodeException::class)
    override suspend fun verifyToken(token: String): Account {
        if (token.isBlank())
            throw IllegalArgumentException("Empty token")

        val payload = verifier.verify(token)

        val claim = payload.getClaim("id")
        if (claim.isNull)
            throw IllegalArgumentException(token)

        return getAccount(claim.asString()) ?: throw NoSuchElementException()
    }

}