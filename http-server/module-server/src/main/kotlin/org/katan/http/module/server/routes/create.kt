package org.katan.http.module.server.routes

import io.ktor.server.application.call
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.util.getOrFail
import org.katan.http.module.server.locations.Servers

internal fun Route.createServer() {
//    val serverService by inject<ServerService>()

    println("create srver")
    post<Servers.Create> {
        println("isde ireq")
        val name = call.parameters.getOrFail("name")

//        val server = try {
//            serverService.create(ServerCreateOptions(name))
//        } catch (e: ServerConflictException) {
//            throwHttpError(SERVER_CONFLICT, HttpStatusCode.Conflict)
//        }
//
//        call.respond(HttpStatusCode.Created, mapOf(
//            "response" to "success",
//            "data" to server
//        ))
    }
}