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
import org.katan.service.fs.FSService
import org.katan.service.fs.http.dto.FSBucketResponse
import org.katan.service.instance.InstanceService
import org.katan.service.instance.http.InstanceRoutes
import org.koin.ktor.ext.inject

internal fun Route.getInstanceFsBucket() {
    val instanceService by inject<InstanceService>()
    val fsService by inject<FSService>()
    val validator by inject<Validator>()

    get<InstanceRoutes.FSBucket> { parameters ->
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
        val matchingBind = runtime.mounts.firstOrNull {
            it.target == parameters.bucket
        } ?: respondError(
            HttpError.UnknownFSBucket,
            HttpStatusCode.Unauthorized,
        )

        val bucket = fsService.getBucket(
            matchingBind.target,
            matchingBind.destination,
        ) ?: respondError(HttpError.UnknownFSBucket)

        respond(FSBucketResponse(bucket))
    }
}
