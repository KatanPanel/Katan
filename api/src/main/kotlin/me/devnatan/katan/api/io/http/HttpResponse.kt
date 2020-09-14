package me.devnatan.katan.api.io.http

/**
 * @property response the server's response, which can be "success" or "error"
 */
sealed class HttpResponse(
    private val response: String
) {

    /**
     * @param T return type of message content
     * @property data content of the response to the request
     */
    class Ok<out T : Any>(
        val data: T
    ) : HttpResponse("success")

    /**
     * @property code error code to better identify it
     * @property message any message that the response may have, usually used for error messages
     */
    class Error(
        val code: Number,
        val message: String
    ) : HttpResponse("error")

}