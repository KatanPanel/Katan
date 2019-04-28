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
import me.devnatan.katan.backend.util.asJsonMap
import me.devnatan.katan.backend.util.asJsonString
import kotlin.system.measureTimeMillis

private fun Application.hooks() {
    log.info("Setupping hooks...")
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(AutoHeadResponse)

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            disableInnerClassSerialization()
            enableComplexMapKeySerialization()

            Katan.gson = create()
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
        Katan.webSocket = this
        incoming.mapNotNull { it as? Frame.Text }.consumeEach { frame ->
            val map = frame.readText().asJsonMap()
            if (map["type"] == "command") {
                when (map["command"]) {
                    "input-server" -> {
                        val server = map["server"]!! as String
                        val serverObj = Katan.serverManager.getServer(server)!!
                        if (serverObj.state == EnumKServerState.RUNNING) {
                            serverObj.write(map["input"] as String)
                            outgoing.send(Frame.Text(mapOf(
                                "type" to "message",
                                "message" to "Command ${map["input"]} written to [$server]."
                            ).asJsonString()))
                        }
                    }
                    "start-server" -> {
                        val server = map["server"]!! as String
                        val serverObj = Katan.serverManager.getServer(server)!!
                        if (serverObj.state == EnumKServerState.STOPPED) {
                            serverObj.startAsync()
                            outgoing.send(Frame.Text(mapOf(
                                "type" to "message",
                                "message" to "Server [$server] started."
                            ).asJsonString()))
                        } else {
                            outgoing.send(Frame.Text(mapOf(
                                "type" to "message",
                                "message" to "Server [$server] already is started."
                            ).asJsonString()))
                        }
                    }
                    else -> {

                    }
                }
            } else if(map["type"] == "server-log") {
                val server = map["server"]!! as String
                val serverObj = Katan.serverManager.getServer(server)!!
                outgoing.send(Frame.Text(mapOf(
                    "type" to "server-log",
                    "server" to server,
                    "message" to serverObj.process.output
                ).asJsonString()))
            }
        }
    }
}

private fun Routing.routes() {
    application.log.info("Creating routes...")
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
                val force = call.request.queryParameters.contains("force")
                server!!.stop(force)
                call.respond(HttpResponse("ok", mapOf("force" to force)))
            }
        }

        get("restart") {
            call.respond(HttpResponse("ok"))
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
        Katan.init()
    }

    log.info("Katan started took ${String.format("%.2f", l.toFloat())}ms")
}