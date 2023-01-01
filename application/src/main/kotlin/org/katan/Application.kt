package org.katan

import kotlinx.coroutines.DEBUG_PROPERTY_NAME
import kotlinx.coroutines.DEBUG_PROPERTY_VALUE_ON
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.di.importAllModules
import org.koin.core.context.startKoin
import java.io.IOException
import java.io.InputStream
import java.util.Properties
import kotlin.reflect.KClass

@Suppress("UNUSED")
private object Application {

    val logger: Logger = LogManager.getLogger(Application::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty(DEBUG_PROPERTY_NAME, DEBUG_PROPERTY_VALUE_ON)

        try {
            readBuildFile()
        } catch (e: IOException) {
            logger.error("Failed to read build file", e)
        }

        startKoin {
            logger(KoinLog4jLogger())
            importAllModules()
        }

        val app = Katan()
        Runtime.getRuntime().addShutdownHook(
            Thread {
                app.close()
            }
        )

        app.start()
    }

    private fun readBuildFile() {
        val build = this::class.readBuildFile()

        System.setProperty("org.katan.build.commit", build.getValue("git.commit.id.abbrev"))
        System.setProperty("org.katan.build.message", build.getValue("git.commit.message.short"))
        System.setProperty("org.katan.build.time", build.getValue("git.commit.time"))
        System.setProperty("org.katan.build.branch", build.getOrDefault("git.branch", ""))
        System.setProperty("org.katan.build.remote", build.getValue("git.remote.origin.url"))
    }

}

private val possiblePaths = arrayOf("build.properties")

private fun KClass<*>.findResource(resource: String): InputStream? {
    return java.classLoader.getResourceAsStream(resource)
}

@Suppress("UNCHECKED_CAST")
fun KClass<*>.readBuildFile(): Map<String, String> {
    for (path in possiblePaths) {
        val res = findResource(path) ?: continue

        return res.use {
            Properties().apply {
                load(it)
            }
        }.toMap() as Map<String, String>
    }

    error("Unable to find build properties: ${possiblePaths.joinToString(", ")}")
}