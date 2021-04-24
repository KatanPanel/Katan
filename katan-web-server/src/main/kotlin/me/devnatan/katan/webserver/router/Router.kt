package me.devnatan.katan.webserver.router

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.websocket.*
import me.devnatan.katan.api.Katan
import me.devnatan.katan.webserver.KatanWS
import me.devnatan.katan.webserver.router.routes.authLoginRoute
import me.devnatan.katan.webserver.router.routes.authRoutes
import me.devnatan.katan.webserver.router.routes.infoRoutes
import me.devnatan.katan.webserver.router.routes.serversRoutes

@OptIn(KtorExperimentalLocationsAPI::class)
fun Application.router(ws: KatanWS) {
    routing {
        webSocket("/") {
            ws.webSocketManager.handleSession(this)
        }

        get<IndexRoute> {
            call.respondText("Running on Katan v${Katan.VERSION} (${ws.katan.environment}).")
        }

        authLoginRoute(ws)
        authenticate {
            infoRoutes(ws)
            authRoutes(ws)
            serversRoutes(ws)
        }
    }
}