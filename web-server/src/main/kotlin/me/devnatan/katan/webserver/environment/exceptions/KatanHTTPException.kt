package me.devnatan.katan.webserver.environment.exceptions

import io.ktor.http.*

class KatanHTTPException(
    val response: Pair<Int, String>,
    val status: HttpStatusCode,
) : RuntimeException()