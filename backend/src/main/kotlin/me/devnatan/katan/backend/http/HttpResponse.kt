package me.devnatan.katan.backend.http

data class HttpResponse(
    val response: String,
    val message: Any? = null
)