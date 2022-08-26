package org.katan.service.unit.instance.http.dto

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.model.fs.Directory
import org.katan.model.fs.VirtualFile

@Serializable
internal data class FSSingleFileResponse(
    val name: String,
    @SerialName("relative-path") val relativePath: String,
    @SerialName("absolute-path") val absolutePath: String,
    val size: Long, // TODO use String to prevent overflow on JS
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
internal data class FSFileResponse(
    val file: FSSingleFileResponse
) {

    internal constructor(file: VirtualFile) : this(FSSingleFileResponse(file))

}

@Serializable
internal data class FSDirectoryResponse(
    val file: FSSingleFileResponse,
    val children: List<FSSingleFileResponse>
) {

    internal constructor(file: Directory) : this(
        FSSingleFileResponse(file),
        file.children.map(::FSSingleFileResponse)
    )

}
