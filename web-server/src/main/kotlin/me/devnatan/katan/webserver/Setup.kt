package me.devnatan.katan.webserver

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.serialization.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import me.devnatan.katan.common.util.get
import me.devnatan.katan.webserver.KatanWebServer.Companion.logger
import org.mpierce.ktor.csrf.CsrfProtection
import org.mpierce.ktor.csrf.OriginMatchesKnownHost

private val kws = KatanWebServer.INSTANCE
internal fun Application.installHooks() {
    install(DefaultHeaders)
    install(AutoHeadResponse)
    install(WebSockets)
    install(Locations)
    install(HSTS) {
        logger.info("Enabled Strict Transport Security")
    }

    install(ContentNegotiation) {
        json(Json(DefaultJson) {
            prettyPrint = true
        })
    }

    install(CallLogging)
    install(StatusPages) {
        exception<Throwable> { cause ->
            call.response.status(HttpStatusCode.InternalServerError)
            throw cause
        }
    }

    install(CORS) {
        allowCredentials = true

        val cors = kws.config.getConfig("cors")
        if (cors.get("allowAnyHost", false)) {
            logger.info("All hosts have been allowed through CORS.")
            anyHost()
        } else if (cors.hasPath("hosts")) {
            logger.info(
                "The following hosts ${
                    cors.getConfigList("hosts").map { config ->
                        Triple(
                            config.getString("hostname"),
                            config.get("schemes", emptyList<String>()),
                            config.get("subDomains", emptyList<String>())
                        )
                    }.onEach { (hostname, schemes, subdomains) ->
                        host(hostname, schemes, subdomains)
                    }.joinToString(", ") { (hostname, schemes, subDomains) ->
                        buildString {
                            append(schemes.joinToString(", ", prefix = "(", postfix = ")"))
                            append("://")

                            if (subDomains.isNotEmpty()) {
                                append(subDomains.joinToString(", ", prefix = "[", postfix = "]"))
                                append(".")
                            }
                            
                            append(hostname)
                        }
                    }
                } have been allowed through in CORS. "
            )
        }
    }

    install(CsrfProtection) {
        logger.info("Whitelisted CSRF hosts: " + kws.config.getConfigList("csrf.hosts").map { host ->
            Triple(host.getString("protocol"), host.getString("hostname"), host.getInt("port"))
        }.onEach { (protocol, hostname, port) ->
            validate(OriginMatchesKnownHost(protocol, hostname, port))
        }.joinToString(", ") { (protocol, hostname, port) ->
            "$protocol://$hostname:$port"
        })
    }

    install(Authentication) {
        val config = kws.config.getConfig("jwt")
        val audience = config.getString("audience")
        jwt {
            realm = "Katan WebServer"
            verifier(
                JWT.require(Algorithm.HMAC256(config.getString("secret")))
                    .withAudience(audience)
                    .withIssuer(config.getString("issuer"))
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(audience))
                    JWTPrincipal(credential.payload)
                else null
            }
        }
    }

}