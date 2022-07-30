package org.katan.http.routes.auth.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class AuthLoginResponse(
    val token: String
)