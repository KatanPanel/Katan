package me.devnatan.katan.api

import java.io.Serializable

/**
 * Descriptors are fundamental objects for the security and reliability of the
 * application, and are used in several areas and in different ways.
 *
 * Every entity within the application must have a descriptor identifying it
 * and revealing whether it is reliable or not.
 *
 * Entities with untrusted descriptors may not have permissions to access
 * certain areas of the application resulting in a [SecurityException].
 *
 * @implNote
 * The properties of a descriptor must be delegated from an
 * [me.devnatan.katan.api.util.InitOnceProperty] and serializable.
 *
 * @author Natan Vieira
 * @since  1.0
 */
interface Descriptor : Serializable {

    /**
     * Returns the entity id of this descriptor.
     */
    val id: String

    /**
     * Returns `true` if the holder of this descriptor is a trustworthy
     * entity or `false` otherwise.
     */
    fun isTrusted(): Boolean

    /**
     * The rule for checking a descriptor's equality is transitive, that if
     * one descriptor has the same id as the other they are equal regardless
     * of the rest of the properties.
     *
     * Returns `true` if this descriptor is equal to [other].
     */
    override fun equals(other: Any?): Boolean

}