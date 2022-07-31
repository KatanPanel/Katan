package org.katan.service.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.katan.model.account.Account
import org.katan.model.account.AccountNotFoundException
import org.katan.model.security.AuthenticationException
import org.katan.model.security.InvalidAccessTokenException
import org.katan.model.security.InvalidCredentialsException
import org.katan.model.security.SaltedHash
import org.katan.model.security.SecurityException
import org.katan.service.account.AccountService
import org.katan.service.auth.crypto.BcryptHash
import org.katan.service.auth.http.InvalidAccessTokenError
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

public interface AuthService {

    public suspend fun auth(username: String, password: String): String

    public suspend fun verify(token: String): Account?
}

internal class AuthServiceImpl(
    private val accountService: AccountService
) : AuthService {

    companion object {
        private val jwtTokenLifetime: Duration = 1.hours
        private const val JWT_ISSUER = "Katan"
    }

    private val algorithm = Algorithm.HMAC256("michjaelJackson")
    private val jwtVerifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer(JWT_ISSUER)
        .build()

    private val hash: SaltedHash = BcryptHash()

    private fun validate(
        input: CharArray,
        hash: String
    ): Boolean {
        if (input.isEmpty() && hash.isEmpty()) {
            return true
        }

        // Catch any exception here to omit sensitive data
        return try {
            this.hash.compare(input, hash)
        } catch (e: Throwable) {
            throw SecurityException("Could not decrypt data.", e)
        }
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
                .withSubject(account.id.toString())
                .sign(algorithm)
        } catch (e: JWTCreationException) {
            throw AuthenticationException("Failed to generate access token", e)
        }
    }

    override suspend fun verify(token: String): Account? {
        val decoded = try {
            jwtVerifier.verify(token)
        } catch (e: TokenExpiredException) {
            throw InvalidAccessTokenException()
        } catch (e: JWTVerificationException) {
            throw AuthenticationException("Failed to verify access token", e)
        }

        return accountService.getAccount(decoded.subject.toLong())
    }
}
