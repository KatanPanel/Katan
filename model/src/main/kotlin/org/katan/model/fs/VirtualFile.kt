package org.katan.model.fs

import kotlinx.datetime.Instant

interface VirtualFile {

    val name: String

    val absolutePath: String

    val size: Long

    val isDirectory: Boolean

    val createdAt: Instant?

    val modifiedAt: Instant?

}