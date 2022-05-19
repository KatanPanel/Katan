package org.katan.http

import io.ktor.http.HttpStatusCode

class KatanHttpException(
    val errorCode: Int,
    val httpStatus: Int,
) : RuntimeException()

fun throwHttpError(errorCode: Int, status: HttpStatusCode): Nothing =
    throw KatanHttpException(errorCode, status.value)