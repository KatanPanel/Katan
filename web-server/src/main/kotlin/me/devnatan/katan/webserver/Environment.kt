package me.devnatan.katan.webserver

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
import io.ktor.response.*
import io.ktor.server.engine.*
import io.ktor.util.*
import io.ktor.websocket.*
import me.devnatan.katan.api.defaultLogLevel
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.common.util.get
import me.devnatan.katan.webserver.exceptions.KatanHTTPException
import me.devnatan.katan.webserver.jwt.AccountPrincipal
import me.devnatan.katan.webserver.websocket.WebSocketManager
import me.devnatan.katan.webserver.websocket.handler.WebSocketServerHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.security.KeyStore
import kotlin.text.toCharArray

class Environment(val server: KatanWS) {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(Environment::class.java)
    }

    lateinit var webSocketManager: WebSocketManager
    lateinit var environment: ApplicationEngineEnvironment

    val config: Config get() = server.config

    fun start() {
        webSocketManager = WebSocketManager().registerEventHandler(WebSocketServerHandler(server.katan))
        environment = applicationEngineEnvironment {
            module {
                installFeatures()
                router(this@Environment)
            }

            val deploy = server.config.getConfig("deployment")
            connector {
                host = deploy.get("host", "localhost")
                port = deploy.get("port", 80)
                logger.info("HTTP connector available at: $host:$port")
            }

            if (deploy.get("ssl.enabled", false)) {
                val ssl = deploy.getConfig("ssl")
                val ks = KeyStore.getInstance(KeyStore.getDefaultType())
                val pass = ssl.getString("keyStorePassword").toCharArray()
                ks.load(null, pass)

                sslConnector(
                    ks,
                    ssl.getString("keyAlias"),
                    { pass },
                    { ssl.getString("privateKeyPassword").toCharArray() }
                ) {
                    port = ssl.get("port", 443)
                    logger.info("HTTPS connector available at: $host:$port")
                }
            }
        }
    }

    suspend fun close() {
        webSocketManager.close()
    }

    @OptIn(KtorExperimentalAPI::class)
    private fun Application.installFeatures() {
        install(Locations)
        install(DefaultHeaders)
        install(AutoHeadResponse)
        install(WebSockets)

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
        }

        install(CORS) {
            method(HttpMethod.Options)
            header("Authorization")
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

        install(Authentication) {
            jwt {
                realm = "Katan WebServer"
                verifier(server.internalAccountManager.verifier)

                validate { credential ->
                    val account = server.internalAccountManager.verifyPayload(credential.payload)
                        ?: respondWithError(INVALID_ACCESS_TOKEN_ERROR, HttpStatusCode.Unauthorized)

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
                decode { values, _ -> server.katan.serverManager.getServer(values.single().toInt()) }
            }
        }

        if (config.get("deployment.secure", false) && config.hasPath("deployment.sslPort")) {
            install(HttpsRedirect) {
                sslPort = config.getInt("deployment.sslPort")
                permanentRedirect = config.get("https-redirect", true)
            }
        }

        if (config.get("hsts", true)) {
            install(HSTS)
            logger.info("Enabled Strict Transport Security (HSTS)")
        }

        if (config.get("under-reverse-proxy", false)) {
            install(ForwardedHeaderSupport)
            logger.info("Enabled Forwarded Header support (for reverse proxy).")
        }
    }

}