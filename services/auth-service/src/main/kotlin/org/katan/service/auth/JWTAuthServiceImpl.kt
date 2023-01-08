package org.katan.service.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTCreationException
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.katan.crypto.SaltedHash
import org.katan.model.account.Account
import org.katan.model.account.AccountNotFoundException
import org.katan.model.security.AuthenticationException
import org.katan.model.security.InvalidCredentialsException
import org.katan.model.security.SecurityException
import org.katan.service.account.AccountService
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

internal class JWTAuthServiceImpl(
    private val accountService: AccountService,
    private val saltedHash: SaltedHash
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
            saltedHash.compare(input, hash)
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
                .withSubject(account.id.toString())
                .sign(algorithm)
        } catch (e: JWTCreationException) {
            throw AuthenticationException("Failed to generate access token", e)
        }
    }

    override suspend fun verify(subject: String?): Account? {
        val id = subject?.toLongOrNull()
            ?: throw AuthenticationException("Invalid account id: $subject")

        return runCatching {
            accountService.getAccount(id)
        }.recoverCatching { exception ->
            throw AuthenticationException("Failed to verify JWT credentials", exception)
        }.getOrNull()
    }
}
