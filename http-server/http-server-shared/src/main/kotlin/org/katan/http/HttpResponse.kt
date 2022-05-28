package org.katan.http

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.util.pipeline.PipelineContext

suspend inline fun PipelineContext<*, ApplicationCall>.respondOk(
    response: Any,
    status: HttpStatusCode = HttpStatusCode.OK
) = call.respond(
    status, mapOf(
        "response" to "success",
        "data" to response
    ) as Map<String, Any?>
)

fun respondError(
    error: HttpError,
    cause: Throwable? = null,
    status: HttpStatusCode = HttpStatusCode.BadRequest
): Nothing = throw KatanHttpException(error.code, error.message, status, cause)