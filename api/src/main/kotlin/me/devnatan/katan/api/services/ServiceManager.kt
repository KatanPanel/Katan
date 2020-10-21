package me.devnatan.katan.api.services

import kotlin.reflect.KClass

/**
 * The service provider is used by the plugins to make values that by logic could only be obtained by defining
 * that plugin as a dependency and taking it directly be accessed easily and quickly.
 *
 * Anything can be registered in the service manager, as long as it has a value and it is not null.
 * It is necessary that whoever is running Katan and who uses the service manager know what happens,
 * why it is not allowed to have more than one service of the same type (conflict).
 *
 * Always check first, using [exists] so that there are no conflicts.
 */
interface ServicesManager {

    /**
     * Returns the value of a registered service.
     * @param service the service to be get.
     * @throws NoSuchElementException if the service is not found.
     */
    fun <T : Any> get(service: KClass<out T>): T

    /**
     * Returns `true` if there is a registered value for that service or `false` otherwise.
     * @param service the service to be checked.
     */
    fun exists(service: KClass<out Any>): Boolean

    /**
     * Registers a new value for the specified service.
     * @param service the service key.
     * @param value the service value.
     */
    fun <T : Any> register(service: KClass<out T>, value: T)

    /**
     * Unregisters a previously registered service.
     * @param service the service to be unregistered.
     */
    fun unregister(service: KClass<out Any>)

}

/**
 * Returns the value of a registered service.
 * @param T the service to be get.
 */
inline fun <reified T : Any> ServicesManager.get(): T? {
    return get(T::class)
}

/**
 * Returns the value of a registered service or [defaultValue] if not registered.
 * @param T the service to be get.
 */
inline fun <reified T : Any> ServicesManager.get(crossinline defaultValue: () -> T): T {
    return get(T::class, defaultValue)
}

/**
 * Returns the value of a registered service or [defaultValue] if not registered.
 * @param service the service to be get.
 */
inline fun <T : Any> ServicesManager.get(service: KClass<out T>, crossinline defaultValue: () -> T): T {
    return runCatching {
        get(service)
    }.getOrElse { defaultValue() }
}