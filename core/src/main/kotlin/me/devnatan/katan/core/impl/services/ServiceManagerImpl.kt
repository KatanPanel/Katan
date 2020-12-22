package me.devnatan.katan.core.impl.services

import me.devnatan.katan.api.Descriptor
import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.service.ServiceManager
import kotlin.reflect.KClass

@OptIn(UnstableKatanApi::class)
class ServiceManagerImpl : ServiceManager {

    private val services: MutableMap<KClass<*>, MutableMap<Descriptor, Any>> = hashMapOf()
    private val lock = Any()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(service: KClass<out T>): T = synchronized(lock) {
        return services.getValue(service).entries.first { it.value == service } as T
    }

    override fun exists(service: KClass<out Any>): Boolean {
        return services.containsKey(service)
    }

    override fun <T : Any> register(service: KClass<out T>, value: T, owner: Descriptor) {
        synchronized(lock) {
            services.computeIfAbsent(service) {
                hashMapOf()
            }.put(owner, value)
        }
    }

    override fun unregister(service: KClass<out Any>) {
        synchronized(lock) {
            services.remove(service)
        }
    }

    override fun unregister(service: KClass<out Any>, owner: Descriptor) {
        synchronized(lock) {
            if (!services.containsKey(service))
                return

            val values = services.getValue(service)
            if (!values.containsKey(owner))
                return

            values.remove(owner)
            if (values.isEmpty())
                services.remove(service)
        }
    }

}