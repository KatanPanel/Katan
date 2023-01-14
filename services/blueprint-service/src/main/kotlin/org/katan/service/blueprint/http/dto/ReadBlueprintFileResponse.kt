package org.katan.service.blueprint.http.dto

import kotlinx.serialization.Serializable
import org.katan.service.fs.http.dto.FSSingleFileResponse

@Serializable
internal data class ReadBlueprintFileResponse(
    val file: FSSingleFileResponse,
    val type: String,
    @Suppress("ArrayInDataClass") val data: ByteArray
)
