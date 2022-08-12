package org.katan.http

import io.ktor.http.HttpStatusCode

open class HttpException(
    val code: Int,
    override val message: String,
    val httpStatus: HttpStatusCode,
    override val cause: Throwable?
) : RuntimeException(cause)
