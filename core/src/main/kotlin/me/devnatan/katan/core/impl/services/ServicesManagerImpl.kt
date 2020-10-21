package me.devnatan.katan.core.impl.services

import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.services.ServicesManager
import kotlin.reflect.KClass

@OptIn(UnstableKatanApi::class)
class ServicesManagerImpl : ServicesManager {

    private val services: MutableMap<KClass<*>, Any> = hashMapOf()
    private val lock = Any()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(service: KClass<out T>): T = synchronized(lock) {
        return services.getValue(service) as T
    }

    override fun exists(service: KClass<out Any>): Boolean {
        return services.containsKey(service)
    }

    override fun <T : Any> register(service: KClass<out T>, value: T) {
        synchronized(lock) {
            services.put(service, value)
        }
    }

    override fun unregister(service: KClass<out Any>) {
        synchronized(lock) {
            services.remove(service)
        }
    }

}