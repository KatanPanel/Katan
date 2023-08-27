package org.katan.service.instance.http.routes

import io.ktor.http.ContentDisposition
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.resources.get
import io.ktor.server.response.header
import io.ktor.server.response.respondFile
import io.ktor.server.routing.Route
import jakarta.validation.Validator
import org.katan.http.response.HttpError
import org.katan.http.response.respondError
import org.katan.http.response.validateOrThrow
import org.katan.model.instance.InstanceNotFoundException
import org.katan.service.fs.FSService
import org.katan.service.instance.InstanceService
import org.katan.service.instance.http.InstanceRoutes
import org.koin.ktor.ext.inject

internal fun Route.readFsFile() {
    val instanceService by inject<InstanceService>()
    val fsService by inject<FSService>()
    val validator by inject<Validator>()

    get<InstanceRoutes.FSReadFile> { parameters ->
        validator.validateOrThrow(parameters)
        val instance = try {
            instanceService.getInstance(parameters.instanceId)
        } catch (_: InstanceNotFoundException) {
            respondError(HttpError.UnknownInstance)
        }

        if (instance.runtime == null) {
            respondError(
                HttpError.InstanceRuntimeNotAvailable,
                HttpStatusCode.ServiceUnavailable,
            )
        }

        // TODO move to instance service to check for bucket reachability
        if (instance.runtime!!.mounts.firstOrNull {
                it.target == parameters.bucket
            } == null
        ) {
            respondError(
                HttpError.ResourceNotAccessible,
                HttpStatusCode.Unauthorized,
            )
        }

        val file = fsService.readFile(
            parameters.path!!,
            parameters.startIndex,
            parameters.endIndex,
        )

        call.response.header(
            HttpHeaders.ContentDisposition,
            ContentDisposition.Mixed.withParameter(
                ContentDisposition.Parameters.FileName,
                file.name,
            ).toString(),
        )
        call.respondFile(file)
    }
}
