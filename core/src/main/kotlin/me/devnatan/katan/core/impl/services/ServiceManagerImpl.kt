package me.devnatan.katan.core.impl.services

import me.devnatan.katan.api.Descriptor
import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.logging.logger
import me.devnatan.katan.api.service.ServiceManager
import kotlin.reflect.KClass

@OptIn(UnstableKatanApi::class)
class ServiceManagerImpl : ServiceManager {

    companion object {

        private val logger = logger<ServiceManager>()

    }

    private class Service(
        val owner: Descriptor,
        val value: Any
    )

    private val services: MutableMap<KClass<*>, MutableList<Service>> = hashMapOf()
    private val lock = Any()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(service: KClass<out T>): List<T> {
        return services[service]?.map { it.value } as? List<T> ?: emptyList()
    }

    override fun exists(service: KClass<out Any>): Boolean {
        return services.containsKey(service)
    }

    override fun <T : Any> register(service: KClass<out T>, value: T, owner: Descriptor) {
        synchronized(lock) {
            services.computeIfAbsent(service) {
                arrayListOf(Service(owner, value))
            }
            logger.debug("Registered ${service.simpleName} to $owner.")
        }
    }

    override fun unregister(service: KClass<out Any>) {
        synchronized(lock) {
            services.remove(service)
            logger.debug("Unregistered ${service.simpleName}.")
        }
    }

    override fun unregister(service: KClass<out Any>, owner: Descriptor) {
        synchronized(lock) {
            if (!services.containsKey(service))
                return

            val iterator = services.getValue(service).iterator()
            while (iterator.hasNext()) {
                val registration = iterator.next()
                if (registration.owner != owner)
                    continue

                iterator.remove()
                logger.debug("Unregistered ${service.simpleName} of $owner.")
            }
        }
    }

}