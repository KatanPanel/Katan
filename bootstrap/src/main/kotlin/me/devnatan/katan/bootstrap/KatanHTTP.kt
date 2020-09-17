package me.devnatan.katan.bootstrap

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import me.devnatan.katan.api.io.http.HttpResponse

private infix fun Int.error(message: String): HttpResponse {
    return HttpResponse.Error(this, message)
}

internal suspend inline fun ApplicationCall.respondWithOk(data: Any) {
    respond(HttpStatusCode.OK, HttpResponse.Ok(data))
}

// ERRORS
val INVALID_ACCESS_TOKEN_ERROR          = 1000 error "Invalid access token"
val ACCOUNT_INVALID_CREDENTIALS_ERROR   = 2002 error "Invalid account username or wrong password"
val ACCOUNT_ALREADY_EXISTS_ERROR        = 2003 error "This account already exists"
val ACCOUNT_NOT_FOUND_ERROR             = 2004 error "Account not found"