package org.katan.service.auth.http.dto

import kotlinx.serialization.Serializable
import org.katan.model.account.Account

@Serializable
internal data class VerificationResponse(
    val account: Account
)