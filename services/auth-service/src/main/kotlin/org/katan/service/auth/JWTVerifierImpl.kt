package org.katan.service.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.AlgorithmMismatchException
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.exceptions.MissingClaimException
import com.auth0.jwt.exceptions.SignatureVerificationException
import com.auth0.jwt.exceptions.TokenExpiredException
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.JWTVerifier
import org.katan.model.security.AuthenticationException

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
