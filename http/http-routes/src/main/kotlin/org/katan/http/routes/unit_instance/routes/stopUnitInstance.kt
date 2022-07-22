package org.katan.http.routes.unit_instance.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.katan.http.routes.unit_instance.UnitInstanceResource
import org.katan.http.routes.unit_instance.getInstanceOrThrow
import org.katan.service.unit.instance.UnitInstanceService
import org.koin.ktor.ext.inject

internal fun Route.stopUnitInstance() {
    val unitInstanceService by inject<UnitInstanceService>()

    get<UnitInstanceResource.Start> { parameters ->
        val instance = getInstanceOrThrow {
            unitInstanceService.getInstance(parameters.instanceId.toLong())
        }

        unitInstanceService.stopInstance(instance)
        call.respond(HttpStatusCode.NoContent)
    }
}