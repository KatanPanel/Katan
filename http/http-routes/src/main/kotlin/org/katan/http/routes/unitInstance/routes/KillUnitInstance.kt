package org.katan.http.routes.unitInstance.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import org.katan.http.routes.unitInstance.UnitInstanceResource
import org.katan.http.routes.unitInstance.getInstanceOrThrow
import org.katan.service.unit.instance.UnitInstanceService
import org.koin.ktor.ext.inject

internal fun Route.killUnitInstance() {
    val unitInstanceService by inject<UnitInstanceService>()

    post<UnitInstanceResource.Kill> { parameters ->
        val instance = getInstanceOrThrow {
            unitInstanceService.getInstance(parameters.instanceId.toLong())
        }

        unitInstanceService.killInstance(instance)
        call.respond(HttpStatusCode.NoContent)
    }
}
