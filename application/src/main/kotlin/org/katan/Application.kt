package org.katan

import kotlinx.coroutines.DEBUG_PROPERTY_NAME
import kotlinx.coroutines.DEBUG_PROPERTY_VALUE_ON
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.di.importAllModules
import org.koin.core.context.startKoin
import java.util.Properties

@Suppress("UNUSED")
private object Application {

    val logger: Logger = LogManager.getLogger(Application::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty(DEBUG_PROPERTY_NAME, DEBUG_PROPERTY_VALUE_ON)

        runCatching {
            readBuildFile()

            startKoin {
                logger(KoinLog4jLogger())
                importAllModules()
            }
        }.onFailure { exception ->
            logger.error("Failed to initialize Katan.", exception)
        }.onSuccess {
            val app = Katan()
            Runtime.getRuntime().addShutdownHook(
                Thread {
                    app.close()
                }
            )

            app.start()
        }
    }

    private fun readBuildFile() {
        var build: Map<String, String>? = null
        for (path in arrayOf("build.properties")) {
            val res = this::class.java.classLoader.getResourceAsStream(path) ?: continue

            @Suppress("UNCHECKED_CAST")
            build = res.use {
                Properties().apply {
                    load(it)
                }
            }.toMap() as Map<String, String>
        }

        if (build == null) {
            return
        }

        System.setProperty("org.katan.build.commit", build.getValue("git.commit.id.abbrev"))
        System.setProperty("org.katan.build.message", build.getValue("git.commit.message.short"))
        System.setProperty("org.katan.build.time", build.getValue("git.commit.time"))
        System.setProperty("org.katan.build.branch", build.getOrDefault("git.branch", ""))
        System.setProperty("org.katan.build.remote", build.getValue("git.remote.origin.url"))
    }
}
