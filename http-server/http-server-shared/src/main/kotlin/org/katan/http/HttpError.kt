package org.katan.http

import kotlinx.serialization.Serializable

@Serializable
data class HttpError(
    val code: Int,
    val message: String
)