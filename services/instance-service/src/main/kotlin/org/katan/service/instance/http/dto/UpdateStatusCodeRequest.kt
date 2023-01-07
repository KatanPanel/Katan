package org.katan.service.instance.http.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class UpdateStatusCodeRequest(val code: Int)
