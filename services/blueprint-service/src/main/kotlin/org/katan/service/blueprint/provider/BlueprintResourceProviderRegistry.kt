package org.katan.service.blueprint.provider

internal class BlueprintResourceProviderRegistry {

    private val registrations: MutableMap<String, BlueprintResourceProvider> = hashMapOf()
    private val lock = Any()

    fun getProvider(id: String): BlueprintResourceProvider? {
        return registrations[id]
    }

    suspend fun findAnyProvider(url: String): BlueprintResourceProvider? {
        return registrations.values.firstOrNull { it.canProvideFrom(url) }
    }

    fun register(provider: BlueprintResourceProvider) {
        synchronized(lock) {
            registrations.put(provider.id, provider)
        }
    }
}
