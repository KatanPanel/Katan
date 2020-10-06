package me.devnatan.katan.api.plugin

interface PluginHandler {

    /**
     * Call this handler for [plugin] in this context for suspended calls without blocking the current thread.
     * @param plugin the plugin
     */
    suspend fun handle(plugin: Plugin)

}