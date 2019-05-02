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
import me.devnatan.katan.backend.message.IncomingMessage
import me.devnatan.katan.backend.message.MessageImpl
import me.devnatan.katan.backend.server.KServer
import me.devnatan.katan.backend.util.asJsonMap
import kotlin.system.measureTimeMillis

val katan = Katan()

private fun Application.hooks() {
    log.info("Setupping hooks...")
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(AutoHeadResponse)

    install(ContentNegotiation) {
        gson {
            disableInnerClassSerialization()
            katan.gson = create()
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
    application.log.info("Initializing WebSocket server...")
    webSocket("/") {
        katan.socketServer.connect(this)

        try {
            incoming.mapNotNull { it as? Frame.Text }.consumeEach { frame ->
                val map = frame.readText().asJsonMap() ?: return@consumeEach
                val message = MessageImpl.fromMap(map) ?: throw IllegalArgumentException("Couldn't handle invalid message")

                katan.messenger.handle(IncomingMessage(message, this))
            }
        } finally {
            katan.socketServer.disconnect(this)
        }
    }
}

private fun Routing.routes() {
    application.log.info("Creating routes...")
    get("/locale") {
        call.respond(HttpResponse("ok", katan.locale))
    }

    route("/listServers") {
        get("/") {
            call.respond(HttpResponse("ok", katan.serverManager.servers))
        }
    }

    route("/server/{serverId}") {
        var server: KServer? = null
        intercept(ApplicationCallPipeline.Features) {
            val serverId = call.parameters["serverId"]
            server = katan.serverManager.getServer(serverId!!)
            if (server == null) {
                context.respond(HttpStatusCode.BadRequest, HttpResponse("error", "Server [$serverId] not found."))
                finish()
            }
        }

        get("/") {
            call.respond(HttpResponse("ok", server))
        }

        get("/logs") {
            call.respond(HttpResponse("ok", server!!.process.output))
        }
    }
}

fun Application.main() {
    val l = measureTimeMillis {
        hooks()
        routing {
            socket()
            routes()
        }

        log.info("Starting...")
        katan.init()
    }

    log.info("Katan started took ${String.format("%.2f", l.toFloat())}ms")
}