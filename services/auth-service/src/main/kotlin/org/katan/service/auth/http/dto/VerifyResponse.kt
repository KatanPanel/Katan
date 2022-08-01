package org.katan.service.auth.http.dto

import kotlinx.serialization.Serializable
import org.katan.service.account.http.dto.AccountResponse

@Serializable
internal data class VerifyResponse(
    val account: AccountResponse
)
