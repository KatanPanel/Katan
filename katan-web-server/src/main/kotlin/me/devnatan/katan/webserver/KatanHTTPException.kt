package me.devnatan.katan.webserver

import io.ktor.http.*

class KatanHTTPException(
    val errorCode: Int,
    val status: HttpStatusCode,
) : RuntimeException()