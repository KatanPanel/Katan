package me.devnatan.katan.webserver

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import me.devnatan.katan.api.security.PasswordCredentials
import me.devnatan.katan.api.security.account.Account
import java.time.Duration
import java.time.Instant
import java.util.*

class TokenManager(val webserver: KatanWS) {

    companion object {

        const val AUTH_SECRET_MIN_LENGTH = 8
        const val AUTH_SECRET_MAX_LENGTH = 32
        private const val JWT_ACCOUNT_CLAIM = "account"
        private val JWT_TOKEN_LIFETIME: Duration = Duration.ofHours(1)

    }

    private val algorithm: Algorithm
    private val audience: String
    val verifier: JWTVerifier

    init {
        val secret = webserver.config.getString("jwt.secret")
        val len = secret.length
        if (len < AUTH_SECRET_MIN_LENGTH)
            throw IllegalArgumentException(
                "JWT secret must have at least " +
                        "$AUTH_SECRET_MIN_LENGTH characters (given: $len). Change this in the web server settings at \"jwt.secret\".)"
            )

        if (len > AUTH_SECRET_MAX_LENGTH)
            throw IllegalArgumentException(
                "JWT secret cannot exceed $AUTH_SECRET_MAX_LENGTH characters " +
                        "(given: $len). Change this in the web server " +
                        "settings at \"jwt.secret\"."
            )

        audience = webserver.config.getString("jwt.audience")
        algorithm = Algorithm.HMAC256(secret)
        verifier = JWT.require(algorithm).withAudience(audience).build()!!
    }

    suspend fun authenticateAccount(
        account: Account,
        password: String
    ): String {
        if (!webserver.katan.accountManager.authenticate(
                account,
                PasswordCredentials(password.toCharArray())
            )
        )
            throw IllegalArgumentException("Wrong password")

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