package me.devnatan.katan.api.plugin

import br.com.devsrsouza.eventkt.EventScope
import br.com.devsrsouza.eventkt.listen
import br.com.devsrsouza.eventkt.scopes.LocalEventScope
import com.typesafe.config.Config
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import me.devnatan.katan.api.Katan
import me.devnatan.katan.api.Version
import me.devnatan.katan.api.command.Command
import me.devnatan.katan.api.security.UntrustedProvider
import me.devnatan.katan.api.util.InitOnceProperty
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.time.Instant
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KClass

/**
 * Plugin is an extension of Katan, plugins have access to several functions
 * that allow you to extract more potential from Katan and create new features
 * without the need for Katan himself to provide this natively.
 *
 * Plugins are handled through a [PluginManager], it has its own state, dependencies, scope and phases.
 */
interface Plugin : UntrustedProvider {

    /**
     * Current instance of the Katan injected into the plugin.
     */
    val katan: Katan

    /**
     * This plugin's private local scope, used to create tasks, receive and send events,
     * wait for requests and others. Canceling this scope will
     * cancel absolutely everything related to tasks in this plugin.
     */
    val coroutineScope: CoroutineScope

    /**
     * The current state of the plugin before, during and after the loading process
     */
    var state: PluginState

    /**
     * Built-in logger built using descriptor data.
     */
    val logger: Logger

    /**
     * Descriptor containing the main information
     * of the plugin such as name, version and others.
     */
    val descriptor: PluginDescriptor

    /**
     * Plugin's dependency manager. A dependency is a PluginDescriptor,
     * which is later converted to a Plugin. Defining a dependency means that you
     * want it to be available during the initialization of your plugin, it will start before it.
     */
    val dependencyManager: PluginDependencyManager

    /**
     * It is the transport of events from the plugin, receives external
     * and internal events and sends internal and external events.
     *
     * It is useful if you have an event-based service that
     * will not need to be directly interlaced as a dependency.
     */
    val eventListener: EventScope

    /**
     * Map of registered handlers for this plugin,
     * calls from the application and the plugin itself must be registered.
     */
    val handlers: MutableMap<PluginPhase, MutableCollection<PluginHandler>>

    /**
     * Plugin working directory.
     */
    val directory: File

    /**
     * The plugin configuration file, which is located in the working [directory].
     */
    val config: Config

}

/**
 * Access the plugin's event listener, through which you can call and listen to events.
 */
inline fun Plugin.listener(crossinline block: EventScope.() -> Unit): EventScope {
    return eventListener.apply(block)
}

/**
 * Listens to a specific event, calling [block] every time the event is published within the scope.
 */
inline fun <reified T : Any> Plugin.event(noinline block: suspend T.() -> Unit) {
    eventListener.listen<T>().onEach(block).launchIn(coroutineScope)
}

/**
 * Adds a handler that when called, executes the [block] function for phase [phase].
 */
fun Plugin.handle(phase: PluginPhase, block: suspend () -> Unit): PluginHandler {
    val handler = PluginHandlerImpl(block)
    handlers.computeIfAbsent(phase) { arrayListOf() }.add(handler)
    return handler
}


/**
 * Access the plugin's dependency manager, it should be used only in case you need a better organization
 * of the dependencies or have direct access to the handler, for just adding dependency use [plugin].
 */
inline fun Plugin.dependencyManagement(crossinline block: PluginDependencyManager.() -> Unit): PluginDependencyManager {
    return dependencyManager.apply(block)
}

/**
 * Delegates a dependency whether it is a plugin or a service.
 */
inline fun <reified T> Plugin.dependency(): ReadOnlyProperty<Plugin, T?> {
    return ReadOnlyProperty { _, _ -> (dependencyManager as GenericPluginDependencyManager).resolveDependency(T::class) as? T }
}

/**
 * Registers a new service in the [ServiceManager] using this plugin as owner.
 * @param service the service to be registered.
 */
inline fun <reified T : Any> Plugin.registerService(service: T) {
    katan.serviceManager.register(T::class, service, descriptor)
}

/**
 * Unregisters a previously registered [service] in the [ServiceManager].
 * @param service the service to be unregistered.
 */
fun Plugin.unregisterService(service: KClass<out Any>) {
    katan.serviceManager.unregister(service, descriptor)
}

// TODO: doc
fun Plugin.registerCommand(command: Command) {
    katan.commandManager.registerCommand(this, command)
}

// TODO: doc
fun Plugin.registerCommands(vararg commands: Command) {
    for (command in commands)
        katan.commandManager.registerCommand(this, command)
}

/**
 * Plugin implementation
 */
open class KatanPlugin(final override val descriptor: PluginDescriptor) : Plugin {

    constructor(
        name: String,
        version: CharSequence? = null,
        author: String? = null
    ) : this(PluginDescriptor(name, version?.let { Version(it) }, author))

    final override val katan: Katan by InitOnceProperty()

    final override val dependencyManager: PluginDependencyManager = GenericPluginDependencyManager()
    final override val directory: File by InitOnceProperty()
    private var _config: () -> Config by InitOnceProperty()
    final override val config: Config get() = _config()
    final override var state: PluginState = PluginState.Unloaded(Instant.now())
    final override val coroutineScope: CoroutineScope = CoroutineScope(CoroutineName("Katan-plugin:${descriptor.name}"))
    final override val eventListener: EventScope = LocalEventScope()
    final override val logger: Logger = LoggerFactory.getLogger(descriptor.name)
    final override val handlers: MutableMap<PluginPhase, MutableCollection<PluginHandler>>

    init {
        // handlers must be available before the plugin starts, the purpose is
        // that they are used in the class or object's initialization block.
        handlers = hashMapOf()
    }

    override fun toString(): String {
        return descriptor.toString()
    }

}