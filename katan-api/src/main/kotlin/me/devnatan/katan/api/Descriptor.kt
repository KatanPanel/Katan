package me.devnatan.katan.api

/**
 * Represents the basic information of a object, as it should be described.
 */
interface Descriptor {

    /**
     * Returns the descriptor name.
     */
    val name: String

    /**
     * Returns `true` if the holder of this descriptor is trusted or` false` otherwise.
     */
    fun isTrusted(): Boolean

}