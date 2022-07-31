package org.katan.http

import io.ktor.http.HttpStatusCode

data class KatanHttpException(
    val code: Int,
    override val message: String,
    val httpStatus: HttpStatusCode,
    override val cause: Throwable?
) : RuntimeException(message, cause)
