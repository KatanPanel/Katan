package me.devnatan.katan.core.manager

import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.devnatan.katan.api.manager.PluginManager
import me.devnatan.katan.api.plugin.KatanPlugin
import me.devnatan.katan.api.plugin.PluginDescriptor
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileFilter
import java.net.URLClassLoader
import java.util.jar.JarFile
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSuperclassOf


class DefaultPluginManager : PluginManager {

    private val logger = LoggerFactory.getLogger(PluginManager::class.java)!!
    private val pwd = File("plugins")

    init {
        if (!pwd.exists())
            pwd.mkdirs()

        logger.debug("Loading plugins...")
        loadPlugin()
    }

    private val mutex = Mutex()
    private val plugins = hashSetOf<KatanPlugin>()

    override fun getPlugin(descriptor: PluginDescriptor): KatanPlugin? {
        return plugins.firstOrNull { it.descriptor == descriptor }
    }

    override suspend fun initializePlugin(descriptor: PluginDescriptor): KatanPlugin {
        return runCatching {
            loadPlugin(descriptor) as KatanPlugin
        }.onSuccess {
            mutex.withLock { plugins.add(it) }
        }.getOrThrow()
    }

    override suspend fun stopPlugin(descriptor: PluginDescriptor): KatanPlugin? {
        val plugin = getPlugin(descriptor) ?: return null
        plugin.coroutineScope.cancel()
        mutex.withLock {
            plugins.remove(plugin)
        }
        return plugin
    }

    private fun loadPlugin(filter: PluginDescriptor? = null): Any? {
        for (file in pwd.listFiles(FileFilter {
            it.extension == "jar"
        }) ?: emptyArray()) {
            JarFile(file).use { jar ->
                val classloader = URLClassLoader(arrayOf(file.toURI().toURL()))
                val entries = jar.entries()
                while (entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    if (entry.isDirectory)
                        continue

                    var name = entry.name
                    if (name.substringAfterLast('.', "") != "class")
                        continue

                    name = name.substringBeforeLast(".").replace("/", ".")
                    logger.info("Checking $name...")
                    val kclass = classloader.loadClass(name).kotlin
                    val instance = retrievePluginInstance(kclass)
                        ?: continue

                    if (filter != null && instance.descriptor != filter)
                        continue

                    logger.info("Found plugin: $instance")
                    runBlocking {
                        plugins.add(instance)
                    }
                }
            }
        }

        return filter?.let {
            throw NoSuchElementException("Plugin not found")
        }
    }

    private fun retrievePluginInstance(kclass: KClass<out Any>): KatanPlugin? {
        fun Any.castAsPlugin(): KatanPlugin? {
            return if (this is KatanPlugin) this
            else null
        }

        return kclass.objectInstance?.castAsPlugin()
            ?: kclass.companionObjectInstance?.castAsPlugin()
            ?: run {
                if (KatanPlugin::class.isSuperclassOf(kclass)) kclass.createInstance().castAsPlugin()
                else null
            }
    }

}