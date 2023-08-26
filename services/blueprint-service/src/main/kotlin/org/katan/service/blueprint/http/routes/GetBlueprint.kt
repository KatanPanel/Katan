package org.katan.service.blueprint.http.routes

import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import jakarta.validation.Validator
import org.katan.http.response.HttpError
import org.katan.http.response.respond
import org.katan.http.response.respondError
import org.katan.http.response.validateOrThrow
import org.katan.model.toSnowflake
import org.katan.service.blueprint.BlueprintNotFoundException
import org.katan.service.blueprint.BlueprintService
import org.katan.service.blueprint.http.BlueprintRoutes
import org.katan.service.blueprint.http.dto.BlueprintResponse
import org.koin.ktor.ext.inject

internal fun Route.getBlueprint() {
    val blueprintService by inject<BlueprintService>()
    val validator by inject<Validator>()

    get<BlueprintRoutes.ById> { parameters ->
        validator.validateOrThrow(parameters)

        val blueprint = try {
            blueprintService.getBlueprint(parameters.blueprintId.toSnowflake())
        } catch (_: BlueprintNotFoundException) {
            respondError(HttpError.UnknownBlueprint)
        }
        respond(BlueprintResponse(blueprint))
    }
}
