package me.devnatan.katan.backend

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
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
    route("/server/{serverId}") {
        get("start") {
            call.respond(HttpStatusCode.OK, HttpResponse("ok"))
        }

        get("stop") {
            call.respond(HttpStatusCode.OK, HttpResponse("ok"))
        }

        get("restart") {
            call.respond(HttpStatusCode.OK, HttpResponse("ok"))
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