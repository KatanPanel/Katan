package org.katan.service.unit.instance.http.routes

import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import jakarta.validation.Validator
import org.katan.http.response.HttpError
import org.katan.http.response.respond
import org.katan.http.response.respondError
import org.katan.http.response.validateOrThrow
import org.katan.service.fs.FSService
import org.katan.service.unit.instance.InstanceNotFoundException
import org.katan.service.unit.instance.UnitInstanceService
import org.katan.service.unit.instance.http.UnitInstanceRoutes
import org.katan.service.unit.instance.http.dto.FSFileListResponse
import org.katan.service.unit.instance.http.dto.FSFileResponse
import org.koin.ktor.ext.inject

internal fun Route.listInstanceFiles() {
    val instanceService by inject<UnitInstanceService>()
    val fsService by inject<FSService>()
    val validator by inject<Validator>()

    get<UnitInstanceRoutes.FSListFiles> { parameters ->
        validator.validateOrThrow(parameters)

        val instance = try {
            instanceService.getInstance(parameters.instanceId.toLong())
        } catch (_: InstanceNotFoundException) {
            respondError(HttpError.UnknownInstance)
        }

        val files = fsService.listFiles(
            buildString {
                append(parameters.bucket)
                parameters.path?.let { path ->
                    append("/")
                    append(path)
                }
            }
        )

        respond(
            FSFileListResponse(
                files = files.map(::FSFileResponse)
            )
        )
    }
}
