package me.devnatan.katan.bootstrap

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import io.ktor.jackson.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.util.*
import io.ktor.websocket.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.mapNotNull
import me.devnatan.katan.api.io.websocket.WebSocketMessage
import me.devnatan.katan.bootstrap.websocket.KtorWebSocketSession
import me.devnatan.katan.core.Katan
import me.devnatan.katan.core.get
import me.devnatan.katan.core.websocket.MutableWebSocketMessage
import org.mpierce.ktor.csrf.CsrfProtection
import org.mpierce.ktor.csrf.OriginMatchesKnownHost
import org.mpierce.ktor.csrf.csrfProtection
import java.io.File
import java.nio.file.Files
import kotlin.system.exitProcess

@OptIn(KtorExperimentalAPI::class)
private class KatanLauncher(config: Config) {

    companion object {

        @JvmStatic
        fun main(args: Array<out String>) {
            val config = File("katan.conf")
            if (!config.exists())
                Files.copy(Katan::class.java.classLoader.getResourceAsStream(config.name)!!, config.toPath())

            KatanLauncher(ConfigFactory.parseFile(config))
        }

    }

    private val katan = Katan(config)

    init {
        runCatching {
            katan.start()
        }.onFailure {
            it.printStackTrace()
            exitProcess(0)
        }

        val deployment = katan.config.getConfig("web.deployment")
        embeddedServer(CIO,
            deployment.get("port", 80),
            deployment.get("host", "0.0.0.0")
        ) {
            installHooks()
            setupRouter()
        }.start(wait = true)
    }

    private fun Application.installHooks() {
        install(DefaultHeaders)
        install(Compression)
        install(AutoHeadResponse)
        install(WebSockets)
        install(Locations)
        install(HSTS)

        install(ContentNegotiation) {
            register(ContentType.Application.Json, JacksonConverter(Katan.objectMapper))
        }

        install(CallLogging)

        install(CORS) {
            Katan.logger.info("Installing CORS protection...")
            allowCredentials = true
            val cors = katan.config.getConfig("web.security.cors")
            if (cors.hasPath("hosts")) {
                for (hostConfig in cors.getConfigList("hosts")) {
                    host(hostConfig.getString("hostname"),
                        hostConfig.getStringList("schemes"),
                        hostConfig.getStringList("subDomains"))
                }
            }

            hosts.forEach { Katan.logger.info("[CORS] Allowed $it.") }
        }

        install(StatusPages) {
            exception<Throwable> { cause ->
                call.response.status(HttpStatusCode.InternalServerError)
                throw cause
            }
        }

        install(CsrfProtection) {
            Katan.logger.info("Installing CSRF protection...")
            for (whitelist in katan.config.getConfigList("web.security.csrf.whitelist")) {
                val protocol = whitelist.getString("protocol")
                val hostname = whitelist.getString("hostname")
                val port = whitelist.getInt("port")
                validate(OriginMatchesKnownHost(protocol, hostname, port))
                Katan.logger.info("[CSRF] Allowed $protocol://$hostname:$port.")
            }
        }
    }

    @OptIn(KtorExperimentalLocationsAPI::class, ExperimentalCoroutinesApi::class)
    private fun Application.setupRouter() = routing {
        webSocket("/") {
            katan.webSocketManager.attachSession(this)
            try {
                incoming.mapNotNull { it as? Frame.Text }.consumeEach { frame ->
                    val data = Katan.objectMapper.readValue(frame.readText(), Map::class.java) ?: return@consumeEach
                    val message = MutableWebSocketMessage(data["id"]!! as Int,
                        data["content"]!!,
                        object : KtorWebSocketSession(this) {
                            override suspend fun send(message: WebSocketMessage) {
                                katan.webSocketManager.writePacket(this, message)
                            }
                        })
                    katan.webSocketManager.emitEvent(message)
                }
            } finally {
                katan.webSocketManager.detachSession(this)
            }
        }


        route("/auth") {
            csrfProtection {
                post<LoginRoute> { account ->
                    if (account.username.isBlank() || account.password.isBlank()) {
                        call.respond(HttpStatusCode.BadRequest, ACCOUNT_INVALID_CREDENTIALS_ERROR)
                        return@post
                    }

                    try {
                        call.respondWithOk(mapOf("token" to katan.accountManager.auth(account.username,
                            account.password)))
                    } catch (e: IllegalArgumentException) {
                        call.respond(HttpStatusCode.BadRequest, ACCOUNT_INVALID_CREDENTIALS_ERROR)
                    } catch (e: NoSuchElementException) {
                        call.respond(HttpStatusCode.BadRequest, ACCOUNT_NOT_FOUND_ERROR)
                    }
                }

                post<RegisterRoute> { account ->
                    if (account.username.isBlank() || account.password.isBlank()) {
                        call.respond(HttpStatusCode.BadRequest, ACCOUNT_INVALID_CREDENTIALS_ERROR)
                        return@post
                    }

                    if (katan.accountManager.existsAccount(account.username)) {
                        call.respond(HttpStatusCode.Conflict, ACCOUNT_ALREADY_EXISTS_ERROR)
                        return@post
                    }

                    val entity = katan.accountManager.createAccount(account.username, account.password)
                    katan.accountManager.registerAccount(entity)
                    call.respondWithOk(mapOf("account" to entity))
                }
            }

            post<VerifyRoute> { verify ->
                if (verify.token.isBlank()) {
                    call.respond(HttpStatusCode.BadRequest, INVALID_ACCESS_TOKEN_ERROR)
                    return@post
                }

                try {
                    call.respondWithOk(mapOf("account" to katan.accountManager.verify(verify.token)))
                } catch (e: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, INVALID_ACCESS_TOKEN_ERROR)
                }
            }
        }
    }

}