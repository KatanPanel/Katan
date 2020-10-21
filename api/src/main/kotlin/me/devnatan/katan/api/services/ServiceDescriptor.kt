package me.devnatan.katan.api.services

import me.devnatan.katan.api.Descriptor
import kotlin.reflect.KClass

/**
 * Represents the identification of a service through its [classifier].
 * @property classifier the service type.
 */
data class ServiceDescriptor(
    val classifier: KClass<out Any>
) : Descriptor {

    override val name: String = classifier.simpleName!!

    override fun toString(): String {
        return name
    }

}