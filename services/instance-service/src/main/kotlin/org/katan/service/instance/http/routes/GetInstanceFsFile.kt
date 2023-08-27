package org.katan.service.instance.http.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import jakarta.validation.Validator
import org.katan.http.response.HttpError
import org.katan.http.response.respond
import org.katan.http.response.respondError
import org.katan.http.response.validateOrThrow
import org.katan.model.instance.InstanceNotFoundException
import org.katan.model.io.BucketNotFoundException
import org.katan.model.io.Directory
import org.katan.model.io.NotAFileException
import org.katan.service.fs.FSService
import org.katan.service.fs.http.dto.FSDirectoryResponse
import org.katan.service.fs.http.dto.FSFileResponse
import org.katan.service.instance.InstanceService
import org.katan.service.instance.http.InstanceRoutes
import org.koin.ktor.ext.inject

internal fun Route.getInstanceFsFile() {
    val instanceService by inject<InstanceService>()
    val fsService by inject<FSService>()
    val validator by inject<Validator>()

    get<InstanceRoutes.FSFile> { parameters ->
        validator.validateOrThrow(parameters)

        val instance = try {
            instanceService.getInstance(parameters.instanceId)
        } catch (_: InstanceNotFoundException) {
            respondError(HttpError.UnknownInstance)
        }

        val runtime = instance.runtime ?: respondError(
            HttpError.InstanceRuntimeNotAvailable,
            HttpStatusCode.ServiceUnavailable,
        )

        // TODO move to instance service to check for bucket reachability
        val matchingBind = runtime.mounts.firstOrNull { mount -> mount.target == parameters.bucket }
            ?: respondError(HttpError.ResourceNotAccessible, HttpStatusCode.Unauthorized)

        val file = try {
            fsService.getFile(
                matchingBind.target,
                matchingBind.destination,
                parameters.path.orEmpty(),
            ) ?: respondError(HttpError.UnknownFSFile)
        } catch (_: BucketNotFoundException) {
            respondError(HttpError.UnknownFSBucket)
        } catch (_: NotAFileException) {
            respondError(HttpError.RequestedResourceIsNotAFile)
        }

        respond(
            if (file is Directory) {
                FSDirectoryResponse(file)
            } else {
                FSFileResponse(file)
            },
        )
    }
}
