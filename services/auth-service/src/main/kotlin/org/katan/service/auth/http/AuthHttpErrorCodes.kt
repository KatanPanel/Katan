package org.katan.service.auth.http

import org.katan.http.HttpError

internal val InvalidAccessTokenError = HttpError(1002, "Invalid access token")

internal val InvalidCredentialsError = HttpError(1003, "Invalid credentials")
