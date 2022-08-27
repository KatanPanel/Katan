package org.katan.service.unit.instance.http.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.contentLength
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import jakarta.validation.Validator
import org.katan.http.response.HttpError
import org.katan.http.response.respondError
import org.katan.http.response.validateOrThrow
import org.katan.service.fs.FSService
import org.katan.service.unit.instance.InstanceNotFoundException
import org.katan.service.unit.instance.UnitInstanceService
import org.katan.service.unit.instance.http.UnitInstanceRoutes
import org.koin.ktor.ext.inject

internal fun Route.readFsFile() {
    val instanceService by inject<UnitInstanceService>()
    val fsService by inject<FSService>()
    val validator by inject<Validator>()

    get<UnitInstanceRoutes.FSReadFile> { parameters ->
        validator.validateOrThrow(parameters)
        val instance = try {
            instanceService.getInstance(parameters.instanceId.toLong())
        } catch (_: InstanceNotFoundException) {
            respondError(HttpError.UnknownInstance)
        }

        if (instance.runtime == null)
            respondError(
                HttpError.InstanceRuntimeNotAvailable,
                HttpStatusCode.ServiceUnavailable
            )

        // TODO move to instance service to check for bucket reachability
        if (instance.runtime!!.mounts.firstOrNull {
                it.target == parameters.bucket
            } == null)
            respondError(
                HttpError.ResourceNotAccessible,
                HttpStatusCode.Unauthorized
            )

        call.respondBytes(
            fsService.readFile(
                parameters.path!!,
                parameters.startIndex,
                parameters.endIndex
            )
        )
    }
}
