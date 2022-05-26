package org.katan.http

import kotlinx.serialization.Serializable

@Serializable
sealed class HttpResponse(val response: String) {

    @Serializable
    data class Success<T>(
        val data: T,
    ) : HttpResponse("success")

    @Serializable
    class Error(
        val code: Int,
        val message: String
    ) : HttpResponse("error")

}

inline fun <reified T> httpResponse(data: T): HttpResponse {
    return HttpResponse.Success(data)
}