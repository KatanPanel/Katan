package org.katan.service.unit.instance.http.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.model.fs.VirtualFile

@Serializable
public data class FSFileResponse(
    val name: String,
    @SerialName("absolute-path") val absolutePath: String,
    val size: Long,
    @SerialName("is-directory") val isDirectory: Boolean,
    @SerialName("created-at") val createdAt: Instant?,
    @SerialName("modified-at") val modifiedAt: Instant?
) {

    internal constructor(file: VirtualFile) : this(
        name = file.name,
        absolutePath = file.absolutePath,
        size = file.size,
        isDirectory = file.isDirectory,
        createdAt = file.createdAt,
        modifiedAt = file.modifiedAt
    )
}

@Serializable
public data class FSFileListResponse(
    val files: List<FSFileResponse>
)
