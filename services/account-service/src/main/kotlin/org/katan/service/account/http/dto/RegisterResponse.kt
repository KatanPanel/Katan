package org.katan.service.account.http.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class RegisterResponse(
    val account: AccountResponse
)
