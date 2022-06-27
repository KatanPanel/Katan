package org.katan.http

import io.ktor.http.HttpStatusCode

open class KatanHttpException(
    val code: Int,
    message: String,
    val httpStatus: HttpStatusCode,
    cause: Throwable?
) : RuntimeException(message, cause)