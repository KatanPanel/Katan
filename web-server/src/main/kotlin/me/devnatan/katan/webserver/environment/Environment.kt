package me.devnatan.katan.webserver.environment

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.typesafe.config.Config
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.jackson.*
import io.ktor.locations.*
import io.ktor.metrics.micrometer.*
import io.ktor.response.*
import io.ktor.server.engine.*
import io.ktor.sessions.*
import io.ktor.util.*
import io.ktor.websocket.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import me.devnatan.katan.api.defaultLogLevel
import me.devnatan.katan.common.util.get
import me.devnatan.katan.webserver.KatanWS
import me.devnatan.katan.webserver.environment.exceptions.KatanHTTPException
import me.devnatan.katan.webserver.websocket.WebSocketManager
import org.mpierce.ktor.csrf.CsrfProtection
import org.mpierce.ktor.csrf.HeaderPresent
import org.mpierce.ktor.csrf.OriginMatchesKnownHost
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.security.KeyStore
import kotlin.text.toCharArray

class Environment(val server: KatanWS) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(Environment::class.java)
        const val XSRF_HEADER = "X-XSRF-TOKEN"
    }

    private var started = false
    lateinit var webSocketManager: WebSocketManager
    lateinit var environment: ApplicationEngineEnvironment

    val config: Config get() = server.config

    fun start() {
        webSocketManager = WebSocketManager()
        environment = applicationEngineEnvironment {
            module {
                installFeatures()
                router(this@Environment)
            }

            val deploy = this@Environment.config.getConfig("deployment")
            connector {
                host = deploy.getString("host")
                port = deploy.getInt("port")
            }

            if (deploy.hasPath("sslPort")) {
                val ssl = deploy.getConfig("ssl")
                val ks = KeyStore.getInstance(KeyStore.getDefaultType())
                val pass = ssl.getString("keyStorePassword").toCharArray()
                ks.load(null, pass)

                sslConnector(
                    ks,
                    ssl.getString("keyAlias"),
                    { pass },
                    { ssl.getString("privateKeyPassword").toCharArray() }
                ) {}
            }
        }
        started = true
    }

    suspend fun close() {
        check(started) { "Katan WS application is not started" }
        webSocketManager.close()
    }

    private fun Application.installFeatures() {
        install(Locations)
        install(DefaultHeaders)
        install(AutoHeadResponse)
        install(WebSockets)
        install(HSTS) {
            logger.info("Enabled Strict Transport Security")
        }

        install(ContentNegotiation) {
            jackson {
                deactivateDefaultTyping()
                enable(SerializationFeature.INDENT_OUTPUT, SerializationFeature.CLOSE_CLOSEABLE)
                disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                setSerializationInclusion(JsonInclude.Include.NON_NULL)
                setDefaultPrettyPrinter(DefaultPrettyPrinter().apply {
                    indentArraysWith(DefaultPrettyPrinter.FixedSpaceIndenter.instance)
                    indentObjectsWith(DefaultIndenter("  ", "\n"))
                })
                propertyNamingStrategy = PropertyNamingStrategy.KEBAB_CASE
            }
        }

        install(CallLogging) {
            if (config.get("logging", true))
                level = server.katan.environment.defaultLogLevel()
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

            exception<Throwable> { cause ->
                call.respond(HttpStatusCode.InternalServerError)
                throw cause
            }
        }

        install(CORS) {
            method(HttpMethod.Options)
            header(XSRF_HEADER)
            allowNonSimpleContentTypes = true
            allowCredentials = true

            val cors = config.getConfig("cors")
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
            validate(HeaderPresent(XSRF_HEADER))
            if (!config.hasPath("csrf.hosts")) {
                logger.info("No hosts are listed as known for CSRF protection.")
                logger.info("All requests from (on specific routes) will require special credentials to access.")
            } else {
                logger.info("Whitelisted CSRF hosts: " + config.getConfigList("csrf.hosts").map { host ->
                    Triple(host.getString("protocol"), host.getString("hostname"), host.getInt("port"))
                }.onEach { (protocol, hostname, port) ->
                    validate(OriginMatchesKnownHost(protocol, hostname, port))
                }.joinToString(", ") { (protocol, hostname, port) ->
                    "$protocol://$hostname:$port"
                })
            }
        }

        install(Authentication) {
            val config = config.getConfig("jwt")
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

        install(Sessions) {
            header<String>(XSRF_HEADER) {
                transform(
                    SessionTransportTransformerMessageAuthentication(
                        hex(config.getString("csrf.key")),
                        "HmacSHA256"
                    )
                )
            }
        }

        install(MicrometerMetrics) {
            registry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT).apply {
                config().commonTags("application", "Katan")
            }
        }
    }

}