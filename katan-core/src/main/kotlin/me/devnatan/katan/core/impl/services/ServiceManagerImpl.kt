package me.devnatan.katan.core.impl.services

import me.devnatan.katan.api.Descriptor
import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.logging.logger
import me.devnatan.katan.api.security.crypto.Hash
import me.devnatan.katan.api.service.ServiceManager
import me.devnatan.katan.common.util.get
import me.devnatan.katan.core.KatanCore
import org.slf4j.Logger
import kotlin.reflect.KClass

@OptIn(UnstableKatanApi::class)
class ServiceManagerImpl(private val core: KatanCore) : ServiceManager {

    companion object {

        private val logger: Logger = logger<ServiceManager>()

    }

    private data class Service(
        val owner: Descriptor,
        val value: Any
    )

    private val services: MutableMap<KClass<*>, MutableList<Service>> = hashMapOf()
    private val lock = Any()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(service: KClass<out T>): List<T> {
        return (services[service]?.map { it.value } as? List<T>).orEmpty()
    }

    override fun exists(service: KClass<out Any>): Boolean {
        return services.containsKey(service)
    }

    override fun <T : Any> register(service: KClass<out T>, value: T, owner: Descriptor) {
        // untrusted plug-in providers cannot get out of here.
        if (!owner.isTrusted() && value is Hash && !core.config.get("security.crypto.allow-external-hash-provider", false))
            throw IllegalArgumentException("External hosting providers are not allowed.")

        synchronized(lock) {
            services.computeIfAbsent(service) {
                arrayListOf()
            }.add(Service(owner, value))
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