@file:OptIn(KtorExperimentalAPI::class)

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
import kotlinx.coroutines.runBlocking
import me.devnatan.katan.bootstrap.routes.LoginRoute
import me.devnatan.katan.bootstrap.routes.RegisterRoute
import me.devnatan.katan.bootstrap.routes.VerifyRoute
import me.devnatan.katan.core.Katan
import me.devnatan.katan.core.websocket.MutableWebSocketMessage
import org.mpierce.ktor.csrf.CsrfProtection
import org.mpierce.ktor.csrf.OriginMatchesKnownHost
import org.mpierce.ktor.csrf.csrfProtection
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.time.Duration
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

fun main() {
    for ((export, clazz) in mapOf("katan.conf" to Katan::class, "application.conf" to KatanLauncher::class)) {
        val file = File(export)
        if (!file.exists())
            Files.copy(clazz.java.classLoader.getResourceAsStream(file.name)!!, file.toPath())
    }

    KatanLauncher(ConfigFactory.load("katan"))
}

private class KatanLauncher(config: Config) {

    private companion object {
        val logger = LoggerFactory.getLogger(KatanLauncher::class.java)!!
    }

    private val katan = Katan(config)

    init {
        val time = measureTimeMillis {
            runBlocking {
                try {
                    katan.start()
                } catch (e: RuntimeException) {
                    exitProcess(0)
                }
            }

            startApplication()
        }

        logger.info("Katan initialized, took {}ms.", String.format("%.2f", Duration.ofMillis(time).seconds))
    }

    private fun startApplication() = embeddedServer(CIO) {
        installHooks()
        setupRouter()
    }.also {
        it.start()
    }

    private fun Application.installHooks() {
        install(DefaultHeaders)
        install(Compression)
        install(AutoHeadResponse)
        install(WebSockets)
        install(Locations)
        install(HSTS)

        install(ContentNegotiation) {
            register(ContentType.Application.Json, JacksonConverter(katan.objectMapper))
        }

        install(CallLogging)

        install(CORS) {
            allowCredentials = true
            anyHost()
        }

        install(StatusPages) {
            exception<Throwable> { cause ->
                call.response.status(HttpStatusCode.InternalServerError)
                throw cause
            }
        }

        install(CsrfProtection) {
            for (whitelist in environment.config.config("ktor.csrf").configList("whitelist"))
                validate(OriginMatchesKnownHost(
                    whitelist.property("protocol").getString(),
                    whitelist.property("hostname").getString(),
                    whitelist.property("port").getString().toInt()
                ))
        }
    }

    @OptIn(KtorExperimentalLocationsAPI::class, ExperimentalCoroutinesApi::class)
    private fun Application.setupRouter() = routing {
        webSocket("/") {
            katan.webSocketManager.attachSession(this)
            try {
                incoming.mapNotNull { it as? Frame.Text }.consumeEach { frame ->
                    val data = katan.objectMapper.readValue(frame.readText(), Map::class.java) ?: return@consumeEach
                    val message = MutableWebSocketMessage(data["id"]!! as Int, data["content"]!!, this)
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

                    val job =
                        katan.accountManager.registerAccountAsync(katan.accountManager.createAccount(account.username,
                            account.password))
                    job.join()
                    call.respondWithOk(mapOf("account" to job.getCompleted()))
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