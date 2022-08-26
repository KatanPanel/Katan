package org.katan.service.unit.instance.http.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.model.fs.VirtualFile

@Serializable
public data class FSSingleFileResponse(
    val name: String,
    @SerialName("relative-path") val relativePath: String,
    @SerialName("absolute-path") val absolutePath: String,
    val size: Long,
    @SerialName("is-directory") val isDirectory: Boolean,
    @SerialName("is-hidden") val isHidden: Boolean,
    @SerialName("created-at") val createdAt: Instant?,
    @SerialName("modified-at") val modifiedAt: Instant?
) {
    internal constructor(file: VirtualFile) : this(
        name = file.name,
        relativePath = file.relativePath,
        absolutePath = file.absolutePath,
        size = file.size,
        isDirectory = file.isDirectory,
        isHidden = file.isHidden,
        createdAt = file.createdAt,
        modifiedAt = file.modifiedAt
    )
}

@Serializable
public data class FSFileResponse(
    val file: FSSingleFileResponse
)

@Serializable
public data class FSDirectoryResponse(
    val files: List<FSSingleFileResponse>
)
