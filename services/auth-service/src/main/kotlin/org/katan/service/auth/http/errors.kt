@file:JvmName("AuthHttpErrorCodes")

package org.katan.service.auth.http

import org.katan.http.HttpError

internal val InvalidAccessTokenError = HttpError(1002, "Invalid access token")

internal val InvalidCredentialsError = HttpError(1003, "Invalid credentials")

internal val UsernameLengthError: (Int, Int) -> HttpError
        = { minLength, maxLength -> HttpError(1004, "Username must have a minimum length of $minLength and at least $maxLength characters") }
