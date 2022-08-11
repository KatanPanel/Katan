package org.katan.http

import kotlinx.serialization.Serializable

@Serializable
data class HttpError internal constructor(
    val code: Int,
    val message: String
) {

    companion object {

        val Generic: (String) -> HttpError = { HttpError(0, it) }
        val UnknownAccount = HttpError(1001, "Unknown account")
        val UnknownUnit = HttpError(1002, "Unknown unit")
        val UnknownInstance = HttpError(1003, "Unknown instance")
        val InvalidAccessToken = HttpError(2001, "Invalid access token")
        val AccountInvalidCredentials = HttpError(2002, "Invalid account credentials")
        val AccountUsernameConflict =
            HttpError(2003, "An account with that username already exists")
        val AccountUsernameLengthConstraints: (Int, Int) -> HttpError = { minLength, maxLength ->
            HttpError(
                2004,
                "Username must have a minimum length of $minLength and at least $maxLength characters"
            )
        }
        val InvalidInstanceUpdateCode = HttpError(3001, "Invalid instance update code")
    }
}
