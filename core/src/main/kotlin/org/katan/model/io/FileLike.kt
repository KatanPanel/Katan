package org.katan.model.io

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
sealed interface FileLike {

    val name: String

    val relativePath: String

    val absolutePath: String

    val size: Long

    val isDirectory: Boolean

    val isHidden: Boolean

    val createdAt: Instant?

    val modifiedAt: Instant?
}

val FileLike.extension: String
    get() = name.substringAfterLast(".", "")

fun FileLike(
    name: String,
    relativePath: String,
    absolutePath: String,
    size: Long,
    isHidden: Boolean,
    createdAt: Instant?,
    modifiedAt: Instant?,
    children: List<FileLike>?,
): FileLike = if (children != null) {
    Directory(
        name = name,
        relativePath = relativePath,
        absolutePath = absolutePath,
        size = size,
        isHidden = isHidden,
        createdAt = createdAt ?: modifiedAt,
        modifiedAt = modifiedAt,
        children = children,
    )
} else {
    File(
        name = name,
        relativePath = relativePath,
        absolutePath = absolutePath,
        size = size,
        isHidden = isHidden,
        createdAt = createdAt ?: modifiedAt,
        modifiedAt = modifiedAt,
    )
}
