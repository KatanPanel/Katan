package org.katan.service.account.http.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class RegisterRequest(
    val username: String,
    val password: String
) {

    companion object {
        const val MIN_USERNAME_LENGTH = 4
        const val MAX_USERNAME_LENGTH = 32
    }

}