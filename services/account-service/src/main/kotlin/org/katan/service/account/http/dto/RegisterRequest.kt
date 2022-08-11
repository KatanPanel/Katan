package org.katan.service.account.http.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import kotlinx.serialization.Serializable

@Serializable
internal data class RegisterRequest(
    @field:NotBlank
    @field:Size(min = 2, max = 48)
    val username: String,

    @field:NotBlank
    val password: String
)
