package org.katan.service.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.AlgorithmMismatchException
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.MissingClaimException
import com.auth0.jwt.exceptions.SignatureVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.JWTVerifier
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.katan.security.Hash
import org.katan.model.account.Account
import org.katan.model.account.AccountNotFoundException
import org.katan.security.AuthenticationException
import org.katan.security.InvalidCredentialsException
import org.katan.security.SecurityException
import org.katan.model.toSnowflake
import org.katan.service.account.AccountService
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

internal class JWTAuthServiceImpl(
    private val accountService: AccountService,
    private val hashAlgorithm: Hash
) : AuthService {

    companion object {
        private val jwtTokenLifetime: Duration = 6.hours
        private const val JWT_ISSUER = "Katan"
    }

    private val algorithm = Algorithm.HMAC256("michjaelJackson")

    private fun validate(input: CharArray, hash: String): Boolean {
        if (input.isEmpty() && hash.isEmpty()) {
            return true
        }

        return runCatching {
            hashAlgorithm.compare(input, hash)
        }.recoverCatching { exception ->
            throw SecurityException("Could not decrypt data.", exception)
        }.getOrThrow()
    }

    override suspend fun auth(username: String, password: String): String {
        val (account, hash) = accountService.getAccountAndHash(username)
            ?: throw AccountNotFoundException()

        if (!validate(input = password.toCharArray(), hash = hash)) {
            throw InvalidCredentialsException()
        }

        val now = Clock.System.now()
        return try {
            JWT.create()
                .withIssuedAt(now.toJavaInstant())
                .withIssuer(JWT_ISSUER)
                .withExpiresAt(now.plus(jwtTokenLifetime).toJavaInstant())
                .withSubject(account.id.value.toString())
                .sign(algorithm)
        } catch (e: JWTCreationException) {
            throw AuthenticationException("Failed to generate access token", e)
        }
    }

    override suspend fun verify(subject: String?): Account? {
        val id = subject?.toLongOrNull()
            ?: throw AuthenticationException("Invalid account id: $subject")

        return runCatching {
            accountService.getAccount(id.toSnowflake())
        }.recoverCatching { exception ->
            throw AuthenticationException("Failed to verify JWT credentials", exception)
        }.getOrNull()
    }
}

internal class JWTVerifierImpl : JWTVerifier {

    companion object {
        private const val JWT_ISSUER = "Katan"
    }

    // TODO generate secret
    private val algorithm = Algorithm.HMAC256("michjaelJackson")

    private val jwtVerifier: com.auth0.jwt.JWTVerifier = JWT.require(algorithm)
        .withIssuer(JWT_ISSUER)
        .build()

    private fun internalVerify(token: String): DecodedJWT {
        return try {
            jwtVerifier.verify(token)
        } catch (e: JWTVerificationException) {
            val message = when (e) {
                is TokenExpiredException -> "Token has expired"
                is SignatureVerificationException -> "Invalid signature"
                is AlgorithmMismatchException -> "Signature algorithm doesn't match"
                is MissingClaimException -> "Missing JWT claim"
                else -> null
            }
            throw AuthenticationException(message, e)
        }
    }

    override fun verify(token: String): DecodedJWT {
        return try {
            internalVerify(token)
        } catch (e: AuthenticationException) {
            throw JWTVerificationException("Access token verification failed", e)
        }
    }

    override fun verify(jwt: DecodedJWT): DecodedJWT {
        return try {
            internalVerify(jwt.token)
        } catch (e: AuthenticationException) {
            throw JWTVerificationException("Access token verification failed", e)
        }
    }
}
