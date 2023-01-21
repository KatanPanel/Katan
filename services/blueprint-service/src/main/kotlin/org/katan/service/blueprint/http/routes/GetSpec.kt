package org.katan.service.blueprint.http.routes

import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import jakarta.validation.Validator
import org.katan.http.response.respond
import org.katan.http.response.validateOrThrow
import org.katan.service.blueprint.BlueprintService
import org.katan.service.blueprint.http.BlueprintRoutes
import org.koin.ktor.ext.inject

internal fun Route.getSpec() {
    val blueprintService by inject<BlueprintService>()
    val validator by inject<Validator>()

    get<BlueprintRoutes.SpecById> { parameters ->
        validator.validateOrThrow(parameters)

        val spec = blueprintService.getSpec(parameters.specId.toLong())
        respond(spec)
    }
}
