package me.devnatan.katan.api.plugin

import kotlinx.coroutines.CoroutineScope
import me.devnatan.katan.api.Katan
import me.devnatan.katan.api.Version

open class KatanPlugin {

    /**
     * Current instance of the Katan injected into the plugin.
     */
    lateinit var katan: Katan

    /**
     * This plugin's private local scope, used to create tasks, receive and send events, wait for requests and others.
     * Canceling this scope will cancel absolutely everything related to tasks in this plugin.
     */
    lateinit var coroutineScope: CoroutineScope

    /**
     * Descriptor containing the main information of the plugin such as name, version and others.
     */
    val descriptor = PluginDescriptor()

    /**
     * Plugin's dependency manager. A dependency is a PluginDescriptor, which is later converted to a Plugin.
     * Defining a dependency means that you want it to be available during
     * the initialization of your plugin, it will start before it.
     */
    lateinit var dependencyManager: DependencyManager

    /**
     * It is the transport of events from the plugin, receives external
     * and internal events and sends internal and external events.
     *
     * It is useful if you have an event-based service that will not need to be directly interlaced as a dependency.
     */
    val eventListener: EventListener

    /**
     * Map of registered handlers for this plugin, calls from the application and the plugin itself must be registered.
     */
    val handlers: MutableMap<PluginPhase, MutableCollection<in PluginHandler>>

    init {
        eventListener = EventListener(coroutineScope)
        handlers = hashMapOf()
    }

}

/**
 * Access the plugin [descriptor] through the [block] function.
 */
inline fun KatanPlugin.descriptor(block: PluginDescriptor.() -> Unit): PluginDescriptor {
    return descriptor.apply(block)
}

/**
 * Access the plugin's descriptor and set its name to [name] and then use the [block] function.
 */
inline fun KatanPlugin.descriptor(name: String, block: PluginDescriptor.() -> Unit = {}): PluginDescriptor {
    return descriptor.apply {
        this.name = name
        block()
    }
}

/**
 * Access the plugin's dependency manager, it should be used only in case you need a better organization
 * of the dependencies or have direct access to the handler, for just adding dependency use [dependsOn].
 */
inline fun KatanPlugin.dependencyManagement(block: DependencyManager.() -> Unit): DependencyManager {
    return dependencyManager.apply(block)
}

/**
 * Adds a plugin that matches the specified [descriptor] by adding
 * it to the plugin's classpath and setting it as the plugin's pre-boot priority.
 * @param descriptor the dependency descriptor
 */
fun KatanPlugin.dependsOn(descriptor: PluginDescriptor) {
    dependencyManager.addDependency(descriptor)
}


/**
 * Adds a plugin that matches the specified descriptor [name] by adding
 * it to the plugin's classpath and setting it as the plugin's pre-boot priority.
 * @param name the dependency name
 */
fun KatanPlugin.dependsOn(name: String) = dependsOn(PluginDescriptor().apply {
    this.name = name
})

/**
 * Adds a plugin that matches the specified descriptor [name] and [version] by adding
 * it to the plugin's classpath and setting it as the plugin's pre-boot priority.
 * @param name the dependency name
 * @param version the dependency version
 */
fun KatanPlugin.dependsOn(name: String, version: Version) = dependsOn(PluginDescriptor().apply {
    this.name = name
    this.version = version
})

/**
 * Access the plugin's event listener, through which you can call and listen to events.
 * @see EventListener
 */
inline fun KatanPlugin.listener(block: EventListener.() -> Unit): EventListener {
    return eventListener.apply(block)
}

/**
 * Adds a [handler] for phase [phase].
 */
fun KatanPlugin.handle(phase: PluginPhase, handler: PluginHandler): PluginHandler {
    return handlers.computeIfAbsent(phase) {
        arrayListOf()
    }.let { handler }
}

/**
 * Adds a handler that, when called, executes the [block] function for phase [phase].
 * @see handle
 */
fun KatanPlugin.handle(phase: PluginPhase, block: KatanPlugin.() -> Unit): PluginHandler {
    return handle(phase, object: PluginHandler {
        override fun handle(plugin: KatanPlugin) = block(plugin)
    })
}


/**
 * Adds a handler that, when called, executes the [block] function for phase [phase].
 * @see handle
 */
fun KatanPlugin.handleSuspending(phase: PluginPhase, block: suspend KatanPlugin.() -> Unit): PluginHandler {
    return handle(phase, object: SuspendablePluginHandler {
        override suspend fun handleSuspending(plugin: KatanPlugin) = block(plugin)
    })
}