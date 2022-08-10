package org.katan.service.unit.instance.http.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class UpdateStatusCodeRequest(
    val code: Int
)