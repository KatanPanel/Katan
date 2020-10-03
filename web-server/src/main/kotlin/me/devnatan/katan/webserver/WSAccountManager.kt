package me.devnatan.katan.webserver

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTDecodeException
import me.devnatan.katan.api.account.Account
import me.devnatan.katan.webserver.KatanWS.Companion.ACCOUNT_TOKEN_PREFIX
import java.time.Duration
import java.time.Instant
import java.util.*
import kotlin.NoSuchElementException

class WSAccountManager(val webserver: KatanWS) {

    companion object {

        const val AUTH_SECRET_MIN_LENGTH = 8
        const val AUTH_SECRET_MAX_LENGTH = 32

        private const val JWT_AUDIENCE = "Katan-AccountManager"
        private val JWT_TOKEN_LIFETIME = Duration.ofMinutes(10)!!

    }

    private val algorithm: Algorithm
    private val verifier: JWTVerifier

    init {
        val secret = webserver.config.getString("jwt.secret")
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
    }

    suspend fun authenticateAccount(account: Account, password: String): String {
        if (!webserver.katan.accountManager.authenticateAccount(account, password))
            throw IllegalArgumentException()

        return JWT.create()
            .withAudience(JWT_AUDIENCE)
            .withClaim("account", account.id.toString())
            .withExpiresAt(Date.from(Instant.now().plus(JWT_TOKEN_LIFETIME)))
            .sign(algorithm)
    }

    @Throws(JWTDecodeException::class)
    suspend fun verifyToken(token: String): Account {
        if (token.isBlank())
            throw IllegalArgumentException("Empty token")

        val payload = verifier.verify(token)

        val claim = payload.getClaim("id")
        if (claim.isNull)
            throw IllegalArgumentException(token)

        return webserver.katan.accountManager.getAccount(claim.asString()) ?: throw NoSuchElementException()
    }

    fun getCachedAccountToken(account: String): String? {
        val cache = webserver.katan.cache
        val key = "$ACCOUNT_TOKEN_PREFIX$account"
        return if (!cache.has(key))
            cache.get(key).toString()
        else null
    }

}