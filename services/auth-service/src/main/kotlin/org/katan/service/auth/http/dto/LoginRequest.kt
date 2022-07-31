package org.katan.service.auth.http.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class LoginRequest(
    val username: String,
    val password: String
)
