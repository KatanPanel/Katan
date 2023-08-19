package org.katan.service.auth.http.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class LoginResponse(val token: String)
