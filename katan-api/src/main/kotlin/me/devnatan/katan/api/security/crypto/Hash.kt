package me.devnatan.katan.api.security.crypto

/**
 * Represents an unsalted hashing algorithm.
 *
 * It is used to apply hashing functions to inputs and also to
 * [compare] values that have already been applied to hashing functions.
 */
interface Hash {

    /**
     * Returns the name of the hashing algorithm.
     */
    val name: String

    /**
     * Creates a hash for the specific input ([value]), can be compared later using the [compare] method
     * @param value the input.
     * @return the input with the hashing function applied.
     */
    fun hash(value: CharArray): String

    /**
     * Checks whether the [value] is valid for the specified [hash].
     * @param value the input.
     * @param hash the hash (obtained through [Hash.hash])
     */
    fun compare(value: CharArray, hash: String): Boolean

}