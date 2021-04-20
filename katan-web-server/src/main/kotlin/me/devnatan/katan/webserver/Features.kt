package me.devnatan.katan.webserver

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.websocket.*
import me.devnatan.katan.api.defaultLogLevel
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.common.EnvKeys
import me.devnatan.katan.common.util.get
import me.devnatan.katan.common.util.getEnvBoolean
import me.devnatan.katan.common.util.getEnvInt
import me.devnatan.katan.webserver.exceptions.KatanHTTPException
import me.devnatan.katan.webserver.jwt.AccountPrincipal
import me.devnatan.katan.webserver.util.respondError

fun Application.installFeatures(ws: KatanWS) {
    install(Locations)
    install(DefaultHeaders)
    install(AutoHeadResponse)
    install(WebSockets)

    install(ContentNegotiation) {
        register(
            ContentType.Application.Json, JacksonConverter(
                KatanWS
                    .objectMapper
            )
        )
    }

    install(CallLogging) {
        if (ws.config.get("logging", true))
            level = ws.katan.environment.defaultLogLevel()
    }

    install(StatusPages) {
        exception<KatanHTTPException> { cause ->
            call.respond(
                cause.status, mapOf(
                    "response" to "error",
                    "code" to cause.response.first,
                    "message" to cause.response.second
                )
            )
        }
    }

    install(CORS) {
        method(HttpMethod.Options)
        header("Authorization")
        allowNonSimpleContentTypes = true
        allowCredentials = true

        val cors = ws.config.getConfig("cors")
        if (cors.get("allowAnyHost", false)) {
            log.info("All hosts have been allowed through CORS.")
            anyHost() // @TODO: Don't do this in production if possible. Try to limit it.
        } else if (cors.hasPath("hosts")) {
            log.info(
                "The following hosts ${
                    cors.getConfigList("hosts").map { config ->
                        Triple(
                            config.getString("hostname"),
                            config.get("schemes", emptyList<String>()),
                            config.get("subDomains", emptyList<String>())
                        )
                    }.onEach { (hostname, schemes, subdomains) ->
                        host(hostname, schemes, subdomains)
                    }
                        .joinToString(", ") { (hostname, schemes, subDomains) ->
                            buildString {
                                append(
                                    schemes.joinToString(
                                        ", ",
                                        prefix = "(",
                                        postfix = ")"
                                    )
                                )
                                append("://")

                                if (subDomains.isNotEmpty()) {
                                    append(
                                        subDomains.joinToString(
                                            ", ",
                                            prefix = "[",
                                            postfix = "]"
                                        )
                                    )
                                    append(".")
                                }

                                append(hostname)
                            }
                        }
                } have been allowed through in CORS. "
            )
        }
    }

    install(Authentication) {
        jwt {
            realm = "Katan WebServer"
            verifier(ws.tokenManager.verifier)

            validate { credential ->
                val account =
                    ws.tokenManager.verifyPayload(credential.payload)
                        ?: respondError(
                            INVALID_ACCESS_TOKEN_ERROR,
                            HttpStatusCode.Unauthorized
                        )

                AccountPrincipal(account)
            }
        }
    }

    install(DataConversion) {
        convert<Server> {
            encode {
                if (it == null) emptyList()
                else listOf((it as Server).id.toString())
            }
            decode { values, _ ->
                ws.katan.serverManager.getServer(
                    values.single().toInt()
                )
            }
        }
    }

    if (ws.config.get("https-redirect", false)) {
        install(HttpsRedirect) {
            sslPort = ws.config.getEnvInt("deployment.ssl.port", EnvKeys.WS_DEPLOY_SSL_PORT)!!
            permanentRedirect = true
        }
    }

    if (ws.config.get("hsts", true)) {
        install(HSTS)
        log.info("Enabled Strict Transport Security (HSTS).")
    }

    if (ws.config.getEnvBoolean("under-reverse-proxy", EnvKeys.WS_REVERSE_PROXIED, false)) {
        install(ForwardedHeaderSupport)
        log.info(
            "Enabled Forwarded Header support (for reverse " +
                    "proxing)."
        )
    }
}