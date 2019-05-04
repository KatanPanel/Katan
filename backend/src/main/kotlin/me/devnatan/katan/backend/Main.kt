package me.devnatan.katan.backend

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
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
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            katan.json = this
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
        call.respond(HttpResponse("ok", "", katan.locale))
    }

    route("/listServers") {
        get("/") {
            call.respond(HttpResponse("ok", "", katan.serverManager.servers))
        }
    }

    post("/createServer") {
        val data = call.receive() as Map<*, *>
        val name = (data["serverName"] as? String)?.trim()
        if (name.isNullOrBlank())
            call.respond(HttpStatusCode.BadRequest, HttpResponse("error", "No server name speficied."))
        else {
            katan.serverManager.getServer(name)?.let {
                call.respond(HttpStatusCode.Conflict, HttpResponse("error", "Server [$name] already exists."))
            } ?: run { val id = katan.serverManager.createServer(katan.coroutine, name, data["address"] as String, (data["port"].toString()).toInt(), (data["memory"].toString()).toInt())
                call.respond(HttpResponse("ok", "Server [$name] created.", mapOf("id" to id)))
            }
        }
    }

    route("/server/{serverId}") {
        var server: KServer? = null
        intercept(ApplicationCallPipeline.Features) {
            val serverId = call.parameters["serverId"]
            server = katan.serverManager.getServer(serverId!!.toInt())
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