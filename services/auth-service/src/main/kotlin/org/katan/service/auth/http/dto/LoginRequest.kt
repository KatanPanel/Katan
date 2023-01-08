package org.katan.service.auth.http.dto

import jakarta.validation.constraints.NotBlank
import kotlinx.serialization.Serializable

@Serializable
internal data class LoginRequest(
    @field:NotBlank(message = "Username must be provided") val username: String = "",
    @field:NotBlank(message = "Password must be provided") val password: String = ""
)
