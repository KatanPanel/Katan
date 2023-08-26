package org.katan.service.blueprint.http.routes

import io.ktor.server.application.call
import io.ktor.server.resources.post
import io.ktor.server.routing.Route
import jakarta.validation.Validator
import org.katan.http.response.receiveValidating
import org.katan.http.response.respond
import org.katan.service.blueprint.BlueprintService
import org.katan.service.blueprint.http.BlueprintRoutes
import org.katan.service.blueprint.http.dto.ImportBlueprintRequest
import org.katan.service.blueprint.importBlueprint
import org.koin.ktor.ext.inject

internal fun Route.importBlueprint() {
    val blueprintService by inject<BlueprintService>()
    val validator by inject<Validator>()

    post<BlueprintRoutes.Import> {
        val req = call.receiveValidating<ImportBlueprintRequest>(validator)
        val spec = blueprintService.importBlueprint(req.url)

        respond(spec)
    }
}
