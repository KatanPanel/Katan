package org.katan.http

import kotlinx.serialization.Serializable

@Serializable
sealed class HttpResponse {
    abstract val response: String

    @Serializable
    data class Success<T>(
        val data: T,
    ) : HttpResponse() {
        override val response: String get() = "success"
    }

    @Serializable
    class Error(
        @Suppress("unused") val code: Int,
        @Suppress("unused") val message: String?
    ) : HttpResponse() {
        override val response: String get() = "error"
    }

}

@Suppress("NOTHING_TO_INLINE")
inline fun <T> httpResponse(data: T): HttpResponse {
    return HttpResponse.Success(data)
}