package me.devnatan.katan.api.io

import me.devnatan.katan.api.Platform
import me.devnatan.katan.api.security.Credential
import java.time.Instant

/**
 * Represents a single file or directory on the file system on that [Platform].
 */
interface File {

    /**
     * Returns the name (with extension) of this [File].
     */
    val name: String

    /**
     * Returns the path to that file.
     */
    val path: String

    /**
     * Returns the last modification that was made to this file or `null` if it has never been modified.
     */
    val lastModification: FileModification?

    /**
     * Returns when the file was created or `null` if the information is not available.
     */
    val createdAt: Instant?

    /**
     * Returns the length of the file.
     */
    val size: Long

    /**
     * Returns if this file is hidden according to the conventions of the underlying [Platform].
     */
    val isHidden: Boolean

    /**
     * Returns `true` if this is a directory or `false` otherwise.
     */
    val isDirectory: Boolean

    /**
     * Returns `true` if this file is protected by any type of [Credential] or `false` otherwise.
     */
    val isProtected: Boolean

}

/**
 * Shortcut to differentiate from [java.io.File].
 */
typealias KFile = File