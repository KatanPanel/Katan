package org.katan.http.response

import kotlinx.serialization.Serializable

@Serializable
data class HttpError internal constructor(
    val code: Int,
    val message: String
) {

    companion object {

        val Generic: (String) -> HttpError = { message -> createError(0, message) }
        val UnknownAccount = createError(1001, "Unknown account")
        val UnknownUnit = createError(1002, "Unknown unit")
        val UnknownInstance = createError(1003, "Unknown instance")
        val InvalidAccessToken = createError(2001, "Invalid access token")
        val AccountInvalidCredentials = createError(2002, "Invalid account credentials")
        val AccountUsernameConflict = createError(
            2003,
            "An account with that username already exists"
        )
        val InvalidInstanceUpdateCode = createError(3001, "Invalid instance update code")
        val FailedToParseRequestBody = createError(3002, "Failed to parse request body")

        @JvmStatic
        fun createError(code: Int, message: String): HttpError {
            return HttpError(code, message)
        }

    }

}
