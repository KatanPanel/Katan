package org.katan.service.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import kotlinx.datetime.Clock
import kotlinx.datetime.toJavaInstant
import org.katan.model.account.Account
import org.katan.model.account.AccountNotFoundException
import org.katan.model.security.Credentials
import org.katan.model.security.InvalidCredentialsException
import org.katan.model.security.SaltedHash
import org.katan.service.account.AccountService
import org.katan.service.auth.crypto.BcryptHash
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

public interface AuthService {

    public fun getIdentifier(): String

    public suspend fun auth(username: String, password: String): String

    public suspend fun verify(token: String): Account?

}

internal class AuthServiceImpl(
    private val accountService: AccountService
) : AuthService {

    companion object {
        private const val ACCOUNT_CLAIM = "acc_id"
        private val jwtTokenLifetime: Duration = 1.hours
    }

    private val algorithm = Algorithm.HMAC256("michjaelJackson")
    private val jwtVerifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer("auth0")
        .build()

    private val hash: SaltedHash = BcryptHash()

    override fun getIdentifier(): String {
        return ACCOUNT_CLAIM;
    }

    private fun validate(
        value: String,
        hashValue: CharArray
    ): Boolean {
        if (hashValue.isEmpty() && value.isEmpty())
            return true

        return hash.compare(hashValue, value)
    }

    override suspend fun auth(username: String, password: String): String {
        val account = accountService.getAccount(username)
            ?: throw AccountNotFoundException(username)

        if (!validate(password, account.hash))
            throw InvalidCredentialsException()

        return JWT.create()
            .withClaim(ACCOUNT_CLAIM, account.id.toString())
            .withExpiresAt(Clock.System.now().plus(jwtTokenLifetime).toJavaInstant())
            .sign(algorithm)
    }

    override suspend fun verify(token: String): Account? {
        val accountId = jwtVerifier.verify(token).getClaim(ACCOUNT_CLAIM).asString()
        return accountService.getAccount(accountId.toLong())
    }

}