package me.devnatan.katan.core.manager

import AlreadyInitializedPropertyException
import InitOnceProperty
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.devnatan.katan.api.manager.PluginManager
import me.devnatan.katan.api.plugin.*
import me.devnatan.katan.core.KatanCore
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileFilter
import java.net.URLClassLoader
import java.time.Instant
import java.util.jar.JarEntry
import java.util.jar.JarFile
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible

@Suppress("BlockingMethodInNonBlockingContext")
class DefaultPluginManager(val katan: KatanCore) : PluginManager {

    private val logger = LoggerFactory.getLogger(PluginManager::class.java)!!
    private val pwd = File("plugins")
    private val mutex: Mutex = Mutex()
    private val plugins: MutableList<Plugin> = arrayListOf()

    init {
        if (!pwd.exists())
            pwd.mkdirs()

        logger.debug("Loading plugins...")
        runBlocking {
            loadPlugins()
        }
    }

    override fun getPlugins(): List<Plugin> {
        return plugins.toList()
    }

    override fun getPlugin(descriptor: PluginDescriptor): Plugin? {
        return plugins.firstOrNull { it.descriptor == descriptor }
    }

    override suspend fun startPlugin(plugin: Plugin) {
        (plugin as KatanPlugin).state = PluginState.Started(Instant.now(), plugin.state as PluginState.Loaded)
        callHandlers(PluginStarted, plugin)
        logger.info("Plugin $plugin started.")
    }

    override suspend fun loadPlugin(descriptor: PluginDescriptor): Plugin {
        return runCatching {
            findPlugin(descriptor)?.let { loadPlugin0(it) }
        }.onSuccess { plugins.add(it!!) }.getOrThrow()!!
    }

    override suspend fun unloadPlugin(plugin: Plugin): Plugin {
        throw NotImplementedError()
    }

    override suspend fun stopPlugin(plugin: Plugin): Plugin? {
        check(plugin.state is PluginState.Started) { "The plugin has not started yet" }
        plugin.coroutineScope.cancel()
        mutex.withLock {
            plugins.remove(plugin)
        }
        return plugin
    }

    private fun findPlugin(descriptor: PluginDescriptor): Plugin? {
        for (file in pwd.listFiles(FileFilter {
            it.extension == "jar"
        }) ?: emptyArray()) {
            JarFile(file).use { jar ->
                val classloader = URLClassLoader(arrayOf(file.toURI().toURL()))
                val entries = jar.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    val plugin = initializePlugin(entry, classloader) ?: continue
                    if (plugin.descriptor == descriptor)
                        return plugin
                }
            }
        }

        return null
    }

    private suspend fun loadPlugin0(plugin: Plugin): Plugin {
        try {
            setProperties(plugin as KatanPlugin, mapOf("katan" to katan))
            plugin.state = PluginState.Loaded(Instant.now(), plugin.state as PluginState.Unloaded)
            callHandlers(PluginLoaded, plugin)
            logger.info("Plugin $plugin loaded")
            return plugin
        } catch (e: AlreadyInitializedPropertyException) {
            logger.error("Could not load plugin \"$plugin\".")
            throw e
        }
    }

    private suspend fun loadPlugins() {
        for (file in pwd.listFiles(FileFilter {
            it.extension == "jar"
        }) ?: emptyArray()) {
            JarFile(file).use { jar ->
                val classloader = URLClassLoader(arrayOf(file.toURI().toURL()))
                val entries = jar.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    val plugin = initializePlugin(entry, classloader)
                    if (plugin != null) {
                        loadPlugin0(plugin)
                        startPlugin(plugin)

                        plugins.add(plugin)
                    }
                }
            }
        }
    }

    private fun initializePlugin(entry: JarEntry, classloader: URLClassLoader): Plugin? {
        if (entry.isDirectory)
            return null

        var name = entry.name
        if (name.substringAfterLast('.', "") != "class")
            return null

        name = name.substringBeforeLast(".").replace("/", ".")

        // prevents inline methods from coming here.
        if (name.contains("$"))
            return null

        return retrievePluginInstance(classloader.loadClass(name).kotlin)
    }

    private fun retrievePluginInstance(kclass: KClass<out Any>): Plugin? {
        fun Any.castAsPlugin(): Plugin? {
            return if (this is Plugin) this
            else null
        }

        if (Plugin::class.isSuperclassOf(kclass))
            return kclass.createInstance().castAsPlugin()

        if (kclass.companionObject != null && Plugin::class.isSuperclassOf(kclass.companionObject!!))
            return kclass.companionObject!!.let { kclass.companionObjectInstance!! }.castAsPlugin()

        return null
    }

    private suspend fun callHandlers(phase: PluginPhase, plugin: Plugin) {
        for (handler in plugin.handlers[phase] ?: emptyList()) {
            handler.handle(plugin)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T, V> setProperties(instance: T, values: Map<String, V>) {
        val members = instance!!::class.memberProperties
        for ((property, value) in values) {
            val field = members.first { it.name == property } as KProperty1<T, V>
            val accessible = field.isAccessible
            if (!accessible)
                field.isAccessible = true

            val delegate = field.getDelegate(instance) as InitOnceProperty<V>
            delegate.setValue(delegate, field, value)
            field.isAccessible = accessible
        }
    }

}