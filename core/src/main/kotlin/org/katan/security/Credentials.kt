package org.katan.security

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface Credentials

@Serializable
@SerialName("token")
data class TokenCredentials(val token: String) : Credentials
