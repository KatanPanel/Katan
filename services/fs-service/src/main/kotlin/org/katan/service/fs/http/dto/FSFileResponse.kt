package org.katan.service.fs.http.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.model.io.Directory
import org.katan.model.io.FileLike

@Serializable
data class FSSingleFileResponse(
    val name: String,
    @SerialName("relative-path") val relativePath: String,
    @SerialName("absolute-path") val absolutePath: String,
    val size: Long, // TODO use String to prevent overflow on clients
    @SerialName("is-directory") val isDirectory: Boolean,
    @SerialName("is-hidden") val isHidden: Boolean,
    @SerialName("created-at") val createdAt: Instant?,
    @SerialName("modified-at") val modifiedAt: Instant?
) {
    constructor(file: FileLike) : this(
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
data class FSFileResponse(val file: FSSingleFileResponse) {

    constructor(file: FileLike) : this(FSSingleFileResponse(file))
}

@Serializable
data class FSDirectoryResponse(val file: FSSingleFileResponse, val children: List<FSSingleFileResponse>) {

    constructor(file: Directory) : this(
        FSSingleFileResponse(file),
        file.children.map(::FSSingleFileResponse)
    )
}
