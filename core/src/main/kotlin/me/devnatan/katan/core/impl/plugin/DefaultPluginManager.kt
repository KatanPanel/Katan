package me.devnatan.katan.core.impl.plugin

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import kotlinx.coroutines.cancel
import me.devnatan.katan.api.plugin.*
import me.devnatan.katan.api.util.InitOnceProperty
import me.devnatan.katan.core.KatanCore
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileFilter
import java.io.FileNotFoundException
import java.net.URLClassLoader
import java.nio.file.Files
import java.time.Instant
import java.util.jar.JarEntry
import java.util.jar.JarFile
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible

@Suppress("BlockingMethodInNonBlockingContext")
class DefaultPluginManager(val katan: KatanCore) : PluginManager {

    companion object {

        const val PLUGIN_CONFIG_FILE_NAME = "config.conf"

    }

    private val logger = LoggerFactory.getLogger(PluginManager::class.java)!!
    private val pwd = File("plugins")
    private val plugins: MutableList<Plugin> = ArrayList()

    init {
        if (!pwd.exists())
            pwd.mkdirs()
    }

    override fun getPlugins() = synchronized(plugins) {
        plugins.toList()
    }

    override fun getPlugin(descriptor: PluginDescriptor): Plugin? {
        return plugins.firstOrNull { it.descriptor == descriptor }
    }

    override suspend fun startPlugin(plugin: Plugin) {
        (plugin as KatanPlugin).state = PluginState.Started(Instant.now(), plugin.state)
        callHandlers(PluginEnabled, plugin)
        logger.info("Plugin \"$plugin\" started.")
    }

    override suspend fun loadPlugin(descriptor: PluginDescriptor): Plugin {
        return runCatching {
            findPlugin(descriptor)?.let { (plugin, classloader) ->
                loadPlugin0(plugin, classloader)
            }
        }.onSuccess {
            synchronized (plugins) {
                plugins.add(it!!)
            }
        }.getOrThrow()!!
    }

    override suspend fun unloadPlugin(plugin: Plugin): Plugin {
        check(plugin.state !is PluginState.Unloaded) { "Plugin is not loaded." }
        plugin.coroutineScope.cancel()
        synchronized (plugins) { plugins.remove(plugin) }
        return plugin
    }

    override suspend fun stopPlugin(plugin: Plugin): Plugin {
        check(plugin.state is PluginState.Started) { "Plugin is not enabled." }
        plugin.coroutineScope.cancel()
        plugin.state = PluginState.Disabled(Instant.now(), plugin.state)
        callHandlers(PluginDisabled, plugin)
        return plugin
    }

    private fun findPlugin(descriptor: PluginDescriptor): Pair<Plugin, URLClassLoader>? {
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
                        return plugin to classloader
                }
            }
        }

        return null
    }

    private suspend fun loadPlugin0(plugin: Plugin, classloader: URLClassLoader): Plugin {
        val workingDir = File(pwd, plugin.descriptor.name)
        val instance = plugin as KatanPlugin
        setDelegateValue(
            instance, mapOf(
                "katan" to katan,
                "directory" to workingDir,
                "_config" to { loadPluginConfig(instance, workingDir, classloader) }
            )
        )
        plugin.state = PluginState.Loaded(Instant.now(), plugin.state)
        callHandlers(PluginLoaded, plugin)
        logger.info("Plugin \"$plugin\" loaded")
        return plugin
    }

    private fun loadPluginConfig(plugin: KatanPlugin, directory: File, classloader: URLClassLoader): Config {
        val resource = classloader.getResourceAsStream(PLUGIN_CONFIG_FILE_NAME)
            ?: throw FileNotFoundException("Tried to access plugin \"${plugin.descriptor.name}\" configuration but the configuration file ($PLUGIN_CONFIG_FILE_NAME) was not found.")

        if (!directory.exists())
            directory.mkdirs()

        val config = File(directory, PLUGIN_CONFIG_FILE_NAME)
        if (!config.exists())
            Files.copy(resource, config.toPath())
        return ConfigFactory.parseFile(config)
    }

    internal suspend fun loadPlugins() {
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
                        loadPlugin0(plugin, classloader)
                        synchronized (plugins) {
                            plugins.add(plugin)
                        }

                        startPlugin(plugin)
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

        // skip inlined members
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
        for (handler in plugin.handlers[phase] ?: return) {
            handler.handle()
        }
    }

    suspend fun callHandlers(phase: PluginPhase) {
        for (plugin in plugins)
            callHandlers(phase, plugin)
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T, V> setDelegateValue(instance: T, values: Map<String, V>) {
        val clazz = instance!!
        val members = clazz::class.memberProperties
        for ((property, value) in values) {
            val field = (members.firstOrNull { it.name == property } as? KProperty1<T, V>)
                ?: clazz::class.superclasses
                    .first { it == KatanPlugin::class }
                    .memberProperties.first { it.name == property } as KProperty1<T, V>

            val accessible = field.isAccessible
            if (!accessible)
                field.isAccessible = true

            val delegate = field.getDelegate(instance) as InitOnceProperty<V>
            delegate.setValue(delegate, field, value)
            field.isAccessible = accessible
        }
    }

    suspend fun disableAll() {
        plugins.forEach { stopPlugin(it) }
    }

}