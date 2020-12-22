package me.devnatan.katan.webserver

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.api.security.credentials.PasswordCredentials
import java.time.Duration
import java.time.Instant
import java.util.*

class TokenManager(val webserver: KatanWS) {

    companion object {

        const val AUTH_SECRET_MIN_LENGTH = 8
        const val AUTH_SECRET_MAX_LENGTH = 32
        private const val JWT_ACCOUNT_CLAIM = "account"
        private val JWT_TOKEN_LIFETIME = Duration.ofHours(1)!!

    }

    private val algorithm: Algorithm
    val verifier: JWTVerifier
    private val audience: String

    init {
        val secret = webserver.config.getString("jwt.secret")
        val len = secret.length
        if (len < AUTH_SECRET_MIN_LENGTH)
            throw IllegalArgumentException("JWT secret must have at least %d characters (given: %d). Change this in the web server settings at \"jwt.secret\".".format(AUTH_SECRET_MIN_LENGTH, len))

        if (len > AUTH_SECRET_MAX_LENGTH)
            throw IllegalArgumentException("JWT secret cannot exceed %d characters (given: %d). Change this in the web server settings at \"jwt.secret\".".format(AUTH_SECRET_MAX_LENGTH, len))

        audience = webserver.config.getString("jwt.audience")
        algorithm = Algorithm.HMAC256(secret)
        verifier = JWT.require(algorithm).withAudience(audience).build()!!
    }

    suspend fun authenticateAccount(account: Account, password: String): String {
        if (!webserver.katan.accountManager.authenticateAccount(account, PasswordCredentials(password)))
            throw IllegalArgumentException()

        return JWT.create()
            .withAudience(audience)
            .withClaim(JWT_ACCOUNT_CLAIM, account.id.toString())
            .withExpiresAt(Date.from(Instant.now().plus(JWT_TOKEN_LIFETIME)))
            .sign(algorithm)
    }

    suspend fun verifyPayload(payload: Payload): Account? {
        return payload.getClaim(JWT_ACCOUNT_CLAIM)?.let {
            webserver.katan.accountManager.getAccount(UUID.fromString(it.asString()))
        }
    }

}