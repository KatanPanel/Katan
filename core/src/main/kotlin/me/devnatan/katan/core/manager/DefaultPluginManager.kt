package me.devnatan.katan.core.manager

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
import java.util.jar.JarEntry
import java.util.jar.JarFile
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSuperclassOf


class DefaultPluginManager(val katan: KatanCore) : PluginManager {

    private val logger = LoggerFactory.getLogger(PluginManager::class.java)!!
    private val pwd = File("plugins")

    init {
        if (!pwd.exists())
            pwd.mkdirs()

        logger.debug("Loading plugins...")
        runBlocking {
            loadPlugins()
        }
    }

    private val mutex = Mutex()
    private val plugins = hashSetOf<Plugin>()

    override fun getPlugin(descriptor: PluginDescriptor): Plugin? {
        return plugins.firstOrNull { it.descriptor == descriptor }
    }

    override suspend fun initializePlugin(descriptor: PluginDescriptor): Plugin {
        return runCatching {
            loadPlugin0(descriptor)
        }.onSuccess {
            mutex.withLock { plugins.add(it!!) }
        }.getOrThrow()!!
    }

    override suspend fun stopPlugin(descriptor: PluginDescriptor): Plugin? {
        val plugin = getPlugin(descriptor) ?: return null
        plugin.coroutineScope.cancel()
        mutex.withLock {
            plugins.remove(plugin)
        }
        return plugin
    }

    private suspend fun loadPlugin(plugin: Plugin) {
        val instance = plugin as KatanPlugin
        val clazz = instance.javaClass
        setProperty(clazz, instance, "katan", katan)
        setProperty(clazz, instance, "dependencyManager", object : DependencyManager {
            override fun addDependency(descriptor: PluginDescriptor) {
                throw NotImplementedError()
            }

            override fun removeDependency(descriptor: PluginDescriptor) {
                throw NotImplementedError()
            }
        })

        callHandlers(PluginLoaded, plugin)
        mutex.withLock {
            plugins.add(instance)
        }

        setProperty(clazz, instance, "state", PluginState.Loaded)
    }

    private fun loadPlugin0(descriptor: PluginDescriptor): Plugin? {
        for (file in pwd.listFiles(FileFilter {
            it.extension == "jar"
        }) ?: emptyArray()) {
            JarFile(file).use { jar ->
                val classloader = URLClassLoader(arrayOf(file.toURI().toURL()))
                val entries = jar.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    val plugin = initializePlugin0(entry, classloader) ?: continue
                    if (plugin.descriptor == descriptor)
                        return plugin
                }
            }
        }

        return null
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
                    val plugin = initializePlugin0(entry, classloader)
                    if (plugin != null) {
                        logger.debug("Loading plugin ${plugin.descriptor}...")
                        loadPlugin(plugin)
                    }
                }
            }
        }
    }

    private fun initializePlugin0(entry: JarEntry, classloader: URLClassLoader): Plugin? {
        if (entry.isDirectory)
            return null

        var name = entry.name
        if (name.substringAfterLast('.', "") != "class")
            return null

        name = name.substringBeforeLast(".").replace("/", ".")
        return retrievePluginInstance(classloader.loadClass(name).kotlin) ?: return null
    }

    private fun callHandlers(phase: PluginPhase, plugin: Plugin) {
        for (handler in plugin.handlers[phase] ?: emptyList()) {
            handler.handle(plugin)
        }
    }

    private fun retrievePluginInstance(kclass: KClass<out Any>): Plugin? {
        fun Any.castAsPlugin(): Plugin? {
            return if (this is Plugin) this
            else null
        }

        return kclass.objectInstance?.castAsPlugin()
            ?: kclass.companionObjectInstance?.castAsPlugin()
            ?: run {
                if (Plugin::class.isSuperclassOf(kclass)) kclass.createInstance().castAsPlugin()
                else null
            }
    }

    private fun setProperty(clazz: Class<*>, obj: Any, property: String, value: Any) {
        val field = clazz.getDeclaredField(property)
        val accessible = field.isAccessible
        if (!accessible)
            field.isAccessible = true

        field.set(obj, value)
        field.isAccessible = accessible
    }

}