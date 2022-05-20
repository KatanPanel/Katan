package org.katan.http

import io.ktor.http.HttpStatusCode

@kotlinx.serialization.Serializable
sealed class HttpResponse(val response: String) {

    data class Success<T>(
        val data: T,
    ) : HttpResponse("success")

    class Error(
        val errorCode: Int
    ) : HttpResponse("error")

}

fun <T> httpResponse(data: T): HttpResponse {
    return HttpResponse.Success(data)
}

@Suppress("NOTHING_TO_INLINE")
inline fun throwHttpException(errorCode: Int, httpStatus: HttpStatusCode): Nothing {
    throw KatanHttpException(errorCode, httpStatus)
}