package org.katan.http

import io.ktor.http.HttpStatusCode

class HttpException(
    val code: Int,
    message: String?,
    val status: HttpStatusCode,
    cause: Throwable?
) : RuntimeException(message, cause)
