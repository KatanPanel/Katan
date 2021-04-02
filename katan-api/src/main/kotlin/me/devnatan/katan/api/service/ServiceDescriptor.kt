package me.devnatan.katan.api.service

import me.devnatan.katan.api.Descriptor
import me.devnatan.katan.api.annotations.InternalKatanApi
import kotlin.reflect.KClass

/**
 * Represents the identification of a service through its [classifier].
 * @property classifier the service type.
 */
@InternalKatanApi
data class ServiceDescriptor(
    val classifier: KClass<out Any>
) : Descriptor {

    override val id: String = classifier.simpleName!!

    // is used for dependency injection differentiation, internal
    override fun isTrusted(): Boolean = true

    override fun toString(): String {
        return id
    }

}