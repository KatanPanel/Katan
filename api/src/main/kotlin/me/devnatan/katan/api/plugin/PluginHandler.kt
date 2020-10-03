package me.devnatan.katan.api.plugin

interface PluginHandler {

    /**
     * Call this handler for [plugin] blocking the current thread.
     * @param plugin the subject
     */
    fun handle(plugin: KatanPlugin)

}

interface SuspendablePluginHandler : PluginHandler {

    override fun handle(plugin: KatanPlugin) {
        throw UnsupportedOperationException()
    }

    /**
     * Call this handler for [plugin] in a unique context
     * for suspended calls without blocking the current thread
     * Use this function to work with blocking or long-running functions.
     * @param plugin the subject
     */
    suspend fun handleSuspending(plugin: KatanPlugin)

}

