package org.katan.service.account.http.dto

import kotlinx.serialization.Serializable
import org.katan.model.account.Account
import org.katan.service.account.codec.AccountSerializer

@Serializable
internal data class RegisterResponse(
    @Serializable(with = AccountSerializer::class) val account: Account
)
