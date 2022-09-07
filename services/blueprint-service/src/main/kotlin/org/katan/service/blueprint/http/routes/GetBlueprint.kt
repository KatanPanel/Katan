package org.katan.service.blueprint.http.routes

import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import jakarta.validation.Validator
import org.katan.http.response.respond
import org.katan.http.response.validateOrThrow
import org.katan.service.blueprint.BlueprintService
import org.katan.service.blueprint.http.BlueprintRoutes
import org.katan.service.blueprint.http.dto.BlueprintResponse
import org.koin.ktor.ext.inject

internal fun Route.getBlueprint() {
    val blueprintService by inject<BlueprintService>()
    val validator by inject<Validator>()

    get<BlueprintRoutes.ById> { parameters ->
        validator.validateOrThrow(parameters)

        val blueprint = blueprintService.getBlueprint(parameters.blueprintId.toLong())
        respond(BlueprintResponse(blueprint))
    }
}
