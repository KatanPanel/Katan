package me.devnatan.katan.api.plugin

/**
 * Plugin dependency manager, responsible for loading, adding and removing plugin dependencies.
 * This can also be intertwined with another dependency management system.
 */
interface DependencyManager {

    /**
     * Adds the dependency from a descriptor.
     * @param descriptor the dependency descriptor
     */
    fun addDependency(descriptor: PluginDescriptor)

    /**
     * Removes a dependency that has been previously added.
     * @param descriptor the dependency descriptor
     */
    fun removeDependency(descriptor: PluginDescriptor)

}