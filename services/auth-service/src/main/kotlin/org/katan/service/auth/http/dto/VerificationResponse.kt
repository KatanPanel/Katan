package org.katan.service.auth.http.dto

import kotlinx.serialization.Serializable
import org.katan.model.account.Account
import org.katan.service.account.codec.AccountSerializer

@Serializable
internal data class VerificationResponse(
    @Serializable(with = AccountSerializer::class) val account: Account
)
