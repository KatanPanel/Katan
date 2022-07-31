package org.katan.service.account.http

import org.katan.http.HttpError

public val AccountNotFound: HttpError =
    HttpError(3001, "Account not found")

internal val AccountConflictError =
    HttpError(10001, "An account with the same username already exists.")

internal val UsernameLengthError: (Int, Int) -> HttpError =
    { minLength, maxLength ->
        HttpError(
            1004,
            "Username must have a minimum length of $minLength and at least $maxLength characters"
        )
    }
