package me.devnatan.katan.backend

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import io.ktor.util.error
import io.ktor.websocket.WebSockets
import io.ktor.websocket.webSocket
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.mapNotNull
import me.devnatan.katan.backend.http.HttpError
import me.devnatan.katan.backend.http.HttpResponse
import me.devnatan.katan.backend.server.EnumKServerState
import me.devnatan.katan.backend.server.KServer
import org.slf4j.Logger
import kotlin.system.measureTimeMillis

private lateinit var logger: Logger

private fun Application.hooks() {
    logger.info("[~] Setup...")
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(AutoHeadResponse)

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            disableInnerClassSerialization()
            enableComplexMapKeySerialization()
        }
    }

    install(CORS) {
        anyHost()
        allowCredentials = true
        listOf(HttpMethod("PATCH"), HttpMethod.Put, HttpMethod.Delete).forEach {
            method(it)
        }
    }

    install(StatusPages) {
        exception<Throwable> { cause ->
            environment.log.error(cause)
            call.respond(
                HttpError(
                    code = HttpStatusCode.InternalServerError,
                    request = call.request.local.uri,
                    message = cause.toString(),
                    cause = cause
                )
            )
        }
    }
    install(WebSockets)
}

private fun Routing.socket() {
    logger.info("[~] WebSocket...")
    webSocket("/") {
        incoming.mapNotNull { it as? Frame.Text }.consumeEach { frame ->
            val json = frame.readText()
            logger.info("WebSocket message: $json")
        }
    }
}

private fun Routing.routes() {
    logger.info("[~] Routing...")
    route("/listServers") {
        get("/") {
            call.respond(HttpResponse("ok", Katan.serverManager.getServers()))
        }
    }

    route("/server/{serverId}") {
        var server: KServer? = null
        intercept(ApplicationCallPipeline.Features) {
            val serverId = call.parameters["serverId"]
            server = Katan.serverManager.getServer(serverId!!)
            if (server == null) {
                context.respond(HttpStatusCode.BadRequest, HttpResponse("error", "Server [$serverId] not found."))
                finish()
            }
        }

        get("/") {
            call.respond(HttpResponse("ok", server))
        }

        get("start") {
            when (server!!.state) {
                EnumKServerState.RUNNING -> call.respond(HttpResponse("error", "Server is already started."))
                EnumKServerState.STARTING -> call.respond(HttpResponse("error", "Server already is starting."))
                else -> {
                    server!!.startAsync()
                    call.respond(HttpResponse("ok"))
                }
            }
        }

        get("stop") {
            if (server!!.state == EnumKServerState.STOPPED)
                call.respond(HttpResponse("error", "Server is not running"))
            else {
                server!!.stop()
                call.respond(HttpResponse("ok"))
            }
        }

        get("restart") {
            call.respond(HttpResponse("ok"))
        }
    }
}

fun Application.main() {
    logger = log

    val l = measureTimeMillis {
        hooks()
        routing {
            socket()
            routes()
        }

        logger.info("[~] Starting...")
        Katan.init(logger)
    }

    logger.info("[+] Katan started took ${String.format("%.2f", l.toFloat())}ms")
}