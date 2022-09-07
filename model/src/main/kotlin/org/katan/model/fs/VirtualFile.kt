package org.katan.model.fs

import kotlinx.datetime.Instant

interface VirtualFile {

    val name: String

    val relativePath: String

    val absolutePath: String

    val size: Long

    val isDirectory: Boolean

    val isHidden: Boolean

    val createdAt: Instant?

    val modifiedAt: Instant?
}

val VirtualFile.extension: String
    get() = name.substringAfterLast(".", "")