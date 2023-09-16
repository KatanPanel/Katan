package org.katan.model.io

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Directory(
    override val name: String,
    override val relativePath: String,
    override val absolutePath: String,
    override val size: Long,
    override val isHidden: Boolean,
    override val createdAt: Instant?,
    override val modifiedAt: Instant?,
    val children: List<VirtualFile>,
) : VirtualFile {

    override val isDirectory: Boolean get() = true
}