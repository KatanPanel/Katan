package org.katan.service.blueprint.http.routes

import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import org.katan.http.response.respond
import org.katan.service.blueprint.BlueprintService
import org.katan.service.blueprint.http.BlueprintRoutes
import org.katan.service.blueprint.http.dto.BlueprintResponse
import org.katan.service.blueprint.http.dto.ListBlueprintsResponse
import org.koin.ktor.ext.inject

internal fun Route.listBlueprints() {
    val blueprintService by inject<BlueprintService>()

    get<BlueprintRoutes.All> {
        val blueprints = blueprintService.listBlueprints()

        respond(
            ListBlueprintsResponse(
                blueprints.map(::BlueprintResponse)
            )
        )
    }
}
