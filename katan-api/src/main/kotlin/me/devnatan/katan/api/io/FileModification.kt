package me.devnatan.katan.api.io

import java.time.Instant

/**
 * Represents a modification made to a [File].
 */
interface FileModification {

    /**
     * Returns the subject of the change.
     */
    val mutator: FileMutator

    /**
     * Returns the [Instant] when the modification occurred.
     */
    val modifiedAt: Instant

    /**
     * Returns the reason why the file was modified.
     */
    val cause: Cause

    /**
     * Represents the cause of the modification of a file.
     */
    sealed class Cause(val type: String) {

        /**
         * The cause of the file being modified is that it has been renamed.
         */
        object Write : Cause("write")

        /**
         * The cause of the file being modified is that it has been renamed.
         */
        class Rename(val oldName: String) : Cause("rename")

    }

}