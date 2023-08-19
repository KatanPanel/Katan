package org.katan.model.io

import kotlinx.datetime.Instant

public interface VirtualFile {

    public val name: String

    public val relativePath: String

    public val absolutePath: String

    public val size: Long

    public val isDirectory: Boolean

    public val isHidden: Boolean

    public val createdAt: Instant?

    public val modifiedAt: Instant?
}

public val VirtualFile.extension: String
    get() = name.substringAfterLast(".", "")
