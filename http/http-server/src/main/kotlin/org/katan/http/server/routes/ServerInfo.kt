package org.katan.http.server.routes

import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.katan.KatanConfig
import org.katan.http.server.dto.ServerInfoBuild
import org.katan.http.server.dto.ServerInfoResponse
import org.koin.ktor.ext.inject

internal fun Route.serverInfo() {
    val config by inject<KatanConfig>()

    get("/") {
        call.respond(
            ServerInfoResponse(
                developmentMode = config.isDevelopment,
                version = config.version,
                nodeId = config.nodeId,
                build = ServerInfoBuild(
                    branch = config.gitBranch.orEmpty(),
                    commit = config.gitCommit.orEmpty()
                )
            )
        )
    }
}
