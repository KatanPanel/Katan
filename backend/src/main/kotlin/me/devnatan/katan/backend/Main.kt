package me.devnatan.katan.backend

import com.fasterxml.jackson.annotation.JsonInclude
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
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.backend.http.HttpError
import me.devnatan.katan.backend.http.HttpResponse
import me.devnatan.katan.backend.message.IncomingMessage
import me.devnatan.katan.backend.message.MessageImpl
import me.devnatan.katan.backend.util.asJsonMap
import me.devnatan.katan.backend.util.readStream
import java.io.File
import java.nio.charset.StandardCharsets
import kotlin.system.measureTimeMillis

val katan = Katan()

const val SERVER_ENDPOINT = "/servers"

private fun Application.hooks() {
    log.info("Setupping hooks...")
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(AutoHeadResponse)

    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
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
                    message = cause.toString()
                )
            )
        }
    }

    install(WebSockets)
}

private fun Routing.socket() {
    application.log.info("Initializing WebSocket server...")
    webSocket("/") {
        katan.socketController.connect(this)

        try {
            incoming.mapNotNull { it as? Frame.Text }.consumeEach { frame ->
                val map = frame.readText().asJsonMap() ?: return@consumeEach
                val message =
                    MessageImpl.fromMap(map) ?: throw IllegalArgumentException("Couldn't handle invalid message")

                katan.messenger.handle(IncomingMessage(message, this))
            }
        } finally {
            katan.socketController.disconnect(this)
        }
    }
}

private fun Routing.routes() {
    application.log.info("Creating routes...")

    get("/locale") {
        call.respond(HttpResponse("ok", content = katan.locale))
    }

    route(SERVER_ENDPOINT) {
        get {
            call.respond(HttpResponse("ok", content = katan.serverController.servers))
        }

        post {
            val data = call.receive() as Map<*, *>
            if (!data.containsKey("serverName") ||
                !data.containsKey("address") ||
                !data.containsKey("port") ||
                !data.containsKey("memory")
            ) {
                call.respond(HttpStatusCode.BadRequest, HttpResponse("error", "No data specified."))
            } else {
                val name = (data["serverName"] as? String)?.trim()
                if (name.isNullOrBlank())
                    call.respond(HttpStatusCode.BadRequest, HttpResponse("error", "No server name speficied."))
                else {
                    katan.serverController.getServer(name)?.let {
                        call.respond(
                            HttpStatusCode.Conflict,
                            HttpResponse("error", "Server [$name] already exists.")
                        )
                    } ?: run {
                        val id = katan.serverController.createServer(
                            name,
                            data["address"] as String,
                            (data["port"].toString()).toInt(),
                            (data["memory"].toString()).toInt()
                        )
                        call.respond(
                            HttpStatusCode.Created,
                            HttpResponse("ok", "Server [$name] created.", mapOf("id" to id))
                        )
                    }
                }
            }
        }

        route("/{server}") {
            var server: Server? = null
            intercept(ApplicationCallPipeline.Features) {
                val serverId = call.parameters["server"]
                try {
                    server = katan.serverController.getServer(serverId?.toInt()!!)
                    if (server == null) {
                        context.respond(HttpStatusCode.NotFound, HttpResponse("error", "Server [$serverId] not found."))
                        finish()
                    }
                } catch (e: NumberFormatException) {
                    finish()
                }
            }

            get {
                call.respond(HttpResponse("ok", content = server))
            }

            get("/logs") {
                call.respond(HttpResponse("ok", content = server?.process!!.output))
            }

            get("/ftp") {
                val file = File(server?.path!!.root, call.parameters["path"] ?: "/")
                when {
                    !file.exists() -> call.respond(
                        HttpStatusCode.NotFound,
                        HttpResponse("error", "Directory not found.")
                    )
                    file.isDirectory -> call.respond(HttpResponse("ok", content = katan.ftp.listFiles(file)))
                    else -> {
                        val io = katan.ftp.fs.readFile(File(server?.path!!.root, call.parameters["path"]), 0)
                        val data = String(io.readStream(), StandardCharsets.UTF_8).trim()
                        call.respond(HttpResponse("ok", content = data))
                    }
                }
            }
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