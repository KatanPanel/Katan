package org.katan.service.instance.http.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.resources.get
import io.ktor.server.routing.Route
import jakarta.validation.Validator
import org.katan.http.response.HttpError
import org.katan.http.response.respond
import org.katan.http.response.respondError
import org.katan.http.response.validateOrThrow
import org.katan.model.fs.BucketNotFoundException
import org.katan.model.fs.Directory
import org.katan.model.fs.NotAFileException
import org.katan.service.fs.FSService
import org.katan.service.instance.InstanceNotFoundException
import org.katan.service.instance.InstanceService
import org.katan.service.instance.http.UnitInstanceRoutes
import org.katan.service.instance.http.dto.FSDirectoryResponse
import org.katan.service.instance.http.dto.FSFileResponse
import org.koin.ktor.ext.inject

internal fun Route.getInstanceFsFile() {
    val instanceService by inject<InstanceService>()
    val fsService by inject<FSService>()
    val validator by inject<Validator>()

    get<UnitInstanceRoutes.FSFile> { parameters ->
        validator.validateOrThrow(parameters)

        val instance = try {
            instanceService.getInstance(parameters.instanceId.toLong())
        } catch (_: InstanceNotFoundException) {
            respondError(HttpError.UnknownInstance)
        }

        val runtime = instance.runtime ?: respondError(
            HttpError.InstanceRuntimeNotAvailable,
            HttpStatusCode.ServiceUnavailable
        )

        // TODO move to instance service to check for bucket reachability
        val matchingBind = runtime.mounts.firstOrNull {
            it.target == parameters.bucket
        } ?: respondError(
            HttpError.ResourceNotAccessible,
            HttpStatusCode.Unauthorized
        )

        val file = try {
            fsService.getFile(
                matchingBind.target,
                matchingBind.destination,
                parameters.path.orEmpty()
            ) ?: respondError(HttpError.UnknownFSFile)
        } catch (e: BucketNotFoundException) {
            respondError(HttpError.UnknownFSBucket)
        } catch (e: NotAFileException) {
            respondError(HttpError.RequestedResourceIsNotAFile)
        }

        respond(
            if (file is Directory) {
                FSDirectoryResponse(file)
            } else {
                FSFileResponse(file)
            }
        )
    }
}
