package me.devnatan.katan.backend.http

import io.ktor.http.HttpStatusCode

data class HttpError(
    val request: String,
    val message: String,
    val code: HttpStatusCode,
    val cause: Throwable? = null
)