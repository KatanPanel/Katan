package org.katan.http.routes.auth.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class AuthLoginRequest(
    val username: String,
    val password: String
)