package org.katan.service.unit.instance.http.routes

import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import jakarta.validation.Validator
import org.katan.http.response.HttpError
import org.katan.http.response.respond
import org.katan.http.response.respondError
import org.katan.http.response.validateOrThrow
import org.katan.model.fs.BucketNotFoundException
import org.katan.service.fs.FSService
import org.katan.service.unit.instance.InstanceNotFoundException
import org.katan.service.unit.instance.UnitInstanceService
import org.katan.service.unit.instance.http.UnitInstanceRoutes
import org.katan.service.unit.instance.http.dto.FSFileResponse
import org.koin.ktor.ext.inject

internal fun Route.getInstanceFile() {
    val instanceService by inject<UnitInstanceService>()
    val fsService by inject<FSService>()
    val validator by inject<Validator>()

    get<UnitInstanceRoutes.FSGetFile> { parameters ->
        if (parameters.path.isNullOrBlank())
            return@get

        validator.validateOrThrow(parameters)

        val instance = try {
            instanceService.getInstance(parameters.instanceId.toLong())
        } catch (_: InstanceNotFoundException) {
            respondError(HttpError.UnknownInstance)
        }

        val file = try {
            fsService.getFile(buildString {
                append(parameters.bucket)
                append("/")
                append(parameters.path)
            })
        } catch (e: BucketNotFoundException) {
            respondError(HttpError.UnknownFSBucket)
        } ?: respondError(HttpError.UnknownFSFile)

        respond(FSFileResponse(file))
    }
}
