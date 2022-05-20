package org.katan.http

import io.ktor.http.HttpStatusCode

class KatanHttpException(
    val errorCode: Int,
    val httpStatus: HttpStatusCode,
) : RuntimeException()