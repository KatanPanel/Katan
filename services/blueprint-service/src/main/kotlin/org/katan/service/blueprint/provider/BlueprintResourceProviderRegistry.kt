package org.katan.service.blueprint.provider

internal object BlueprintResourceProviderRegistry {

    private val registrations: MutableMap<String, BlueprintResourceProvider> = hashMapOf()
    private val lock = Any()

    fun register(name: String, provider: BlueprintResourceProvider) {
        synchronized(lock) {
            registrations.put(name, provider)
        }
    }

}