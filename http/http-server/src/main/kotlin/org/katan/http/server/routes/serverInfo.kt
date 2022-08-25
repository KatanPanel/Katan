package org.katan.http.server.routes

import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.datetime.Instant
import org.katan.config.KatanConfig
import org.katan.http.server.dto.ServerInfoBuild
import org.katan.http.server.dto.ServerInfoNetworkResponse
import org.katan.http.server.dto.ServerInfoResponse
import org.koin.ktor.ext.inject

private fun buildProperty(name: String) = System.getProperty("org.katan.build.$name")

internal fun Route.serverInfo() {
    val config by inject<KatanConfig>()

    get("/") {
        call.respond(
            ServerInfoResponse(
                version = System.getProperty("org.katan.version", "unknown"),
                nodeId = config.nodeId,
                clusterMode = false,
                defaultNetwork = ServerInfoNetworkResponse(
                    name = config.docker.network.name,
                    driver = config.docker.network.driver
                ),
                build = ServerInfoBuild(
                    branch = buildProperty("branch"),
                    commit = buildProperty("commit"),
                    message = buildProperty("message"),
                    remote = buildProperty("remote"),
                    time = Instant.parse(buildProperty("time"))
                )
            )
        )
    }
}