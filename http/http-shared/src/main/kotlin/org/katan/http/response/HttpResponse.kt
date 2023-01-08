package org.katan.http.response

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.util.pipeline.PipelineContext
import jakarta.validation.Validator
import org.katan.http.HttpException

suspend inline fun PipelineContext<*, ApplicationCall>.respond(
    response: Any,
    status: HttpStatusCode = HttpStatusCode.OK
): Unit = call.respond(status, response)

suspend inline fun <reified T : Any> ApplicationCall.receiveValidating(validator: Validator): T {
    val req = receive<T>()
    validator.validateOrThrow(req)
    return req
}

fun respondError(
    error: HttpError,
    status: HttpStatusCode = HttpStatusCode.BadRequest,
    cause: Throwable? = null
): Nothing = throw HttpException(error.code, error.message, error.details, status, cause)
