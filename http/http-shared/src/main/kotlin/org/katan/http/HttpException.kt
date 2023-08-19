package org.katan.http

import io.ktor.http.HttpStatusCode
import org.katan.http.response.HttpError

class HttpException(
    val code: Int,
    message: String?,
    val details: String?,
    val status: HttpStatusCode,
    cause: Throwable?
) : RuntimeException(message, cause)