package org.katan.http

import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.MissingRequestParameterException

open class KatanHttpException(
    val code: Int,
    message: String,
    val httpStatus: HttpStatusCode,
    cause: Throwable?
) : RuntimeException(message, cause)

fun throwHttpException(
    error: HttpError,
    httpStatus: HttpStatusCode = HttpStatusCode.BadRequest,
    cause: Throwable? = null
): Nothing {
    throw KatanHttpException(error.code, error.message, httpStatus, cause)
}

fun throwMissingParameter(parameterName: String): Nothing {
    throw MissingRequestParameterException(parameterName)
}