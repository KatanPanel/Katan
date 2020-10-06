package me.devnatan.katan.api.plugin

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import me.devnatan.katan.api.Katan
import me.devnatan.katan.api.Version
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap

interface Plugin {

    /**
     * Current instance of the Katan injected into the plugin.
     */
    val katan: Katan

    /**
     * Built-in logger built using descriptor data.
     */
    val logger: Logger

    /**
     * This plugin's private local scope, used to create tasks, receive and send events, wait for requests and others.
     * Canceling this scope will cancel absolutely everything related to tasks in this plugin.
     */
    val coroutineScope: CoroutineScope

    /**
     * Descriptor containing the main information of the plugin such as name, version and others.
     */
    val descriptor: PluginDescriptor

    /**
     * Plugin's dependency manager. A dependency is a PluginDescriptor, which is later converted to a Plugin.
     * Defining a dependency means that you want it to be available during
     * the initialization of your plugin, it will start before it.
     */
    val dependencyManager: DependencyManager

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
    val handlers: MutableMap<PluginPhase, MutableCollection<PluginHandler>>

    /**
     * The current state of the plugin before, during and after the loading process
     */
    val state: PluginState

}

open class KatanPlugin : Plugin {

    final override val katan get() = uninitialized()
    final override val state get() = uninitialized()
    final override val dependencyManager get() = uninitialized()
    internal var _descriptor: PluginDescriptor? = null
    final override val descriptor: PluginDescriptor
        get() = _descriptor ?: uninitialized()
    final override val logger: Logger
    final override val coroutineScope: CoroutineScope
    final override val eventListener: EventListener
    final override val handlers: MutableMap<PluginPhase, MutableCollection<PluginHandler>>

    init {
        logger = LoggerFactory.getLogger(descriptor.name)
        coroutineScope = CoroutineScope(CoroutineName("Katan::plugin-${descriptor.name}"))
        eventListener = EventListener(coroutineScope)
        handlers = ConcurrentHashMap()
    }

    private fun uninitialized(): Nothing {
        throw IllegalStateException("Not yet initialized")
    }

}

/**
 * Set the plugin name to [name], version to [version] and [author] to author.
 */
fun KatanPlugin.descriptor(name: String, version: CharSequence? = null, author: String? = null): PluginDescriptor {
    if (_descriptor != null)
        throw IllegalStateException()

    return PluginDescriptor(name, version?.let { Version(it) }, author).also { _descriptor = it }
}

/**
 * Set the plugin name to [name], version to [version] and [author] to author.
 */
fun KatanPlugin.descriptor(name: String, version: Version? = null, author: String? = null): PluginDescriptor {
    if (_descriptor != null)
        throw IllegalStateException()

    return PluginDescriptor(name, version, author).also { _descriptor = it }
}

/**
 * Access the plugin's dependency manager, it should be used only in case you need a better organization
 * of the dependencies or have direct access to the handler, for just adding dependency use [dependsOn].
 */
inline fun Plugin.dependencyManagement(block: DependencyManager.() -> Unit): DependencyManager {
    return dependencyManager.apply(block)
}

/**
 * Adds a plugin that matches the specified [descriptor] by adding
 * it to the plugin's classpath and setting it as the plugin's pre-boot priority.
 * @param descriptor the dependency descriptor
 */
fun Plugin.dependsOn(descriptor: PluginDescriptor) {
    dependencyManager.addDependency(descriptor)
}


/**
 * Adds a plugin that matches the specified descriptor [name] by adding
 * it to the plugin's classpath and setting it as the plugin's pre-boot priority.
 * @param name the dependency name
 */
fun Plugin.dependsOn(name: String) = dependsOn(PluginDescriptor(name))

/**
 * Adds a plugin that matches the specified descriptor [name] and [version] by adding
 * it to the plugin's classpath and setting it as the plugin's pre-boot priority.
 * @param name the dependency name
 * @param version the dependency version
 */
fun Plugin.dependsOn(name: String, version: CharSequence) = dependsOn(PluginDescriptor(name, Version(version)))

/**
 * Adds a plugin that matches the specified descriptor [name] and [version] by adding
 * it to the plugin's classpath and setting it as the plugin's pre-boot priority.
 * @param name the dependency name
 * @param version the dependency version
 */
fun Plugin.dependsOn(name: String, version: Version) = dependsOn(PluginDescriptor(name, version))

/**
 * Access the plugin's event listener, through which you can call and listen to events.
 * @see EventListener
 */
inline fun Plugin.listener(block: EventListener.() -> Unit): EventListener {
    return eventListener.apply(block)
}

/**
 * Adds a [handler] for phase [phase].
 */
fun Plugin.handle(phase: PluginPhase, handler: PluginHandler): PluginHandler {
    return handlers.computeIfAbsent(phase) {
        arrayListOf()
    }.let { handler }
}

/**
 * Adds a handler that, when called, executes the [block] function for phase [phase].
 * @see handle
 */
fun Plugin.handle(phase: PluginPhase, block: Plugin.() -> Unit): PluginHandler {
    return handle(phase, object : PluginHandler {
        override fun handle(plugin: Plugin) = block(plugin)
    })
}

/**
 * Adds a handler that, when called, executes the [block] function for phase [phase].
 * @see handle
 */
fun Plugin.handleSuspending(phase: PluginPhase, block: suspend Plugin.() -> Unit): PluginHandler {
    return handle(phase, object : SuspendablePluginHandler {
        override suspend fun handleSuspending(plugin: Plugin) = block(plugin)
    })
}

/**
 * Adds a handler that, when called, executes the [block] function for phase [phase].
 * @see handle
 */
fun Plugin.handle(phase: PluginPhase, block: () -> Unit): PluginHandler {
    return handle(phase, object : PluginHandler {
        override fun handle(plugin: Plugin) = block()
    })
}

/**
 * Adds a handler that, when called, executes the [block] function for phase [phase].
 * @see handle
 */
fun Plugin.handleSuspending(phase: PluginPhase, block: suspend () -> Unit): PluginHandler {
    return handle(phase, object : SuspendablePluginHandler {
        override suspend fun handleSuspending(plugin: Plugin) = block()
    })
}