package org.katan.service.fs.impl

import kotlinx.datetime.Instant
import org.katan.model.io.VirtualFile

public data class FileImpl(
    override val name: String,
    override val relativePath: String,
    override val absolutePath: String,
    override val size: Long,
    override val isDirectory: Boolean,
    override val isHidden: Boolean,
    override val createdAt: Instant?,
    override val modifiedAt: Instant?
) : VirtualFile
