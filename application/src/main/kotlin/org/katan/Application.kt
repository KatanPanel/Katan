package org.katan

import kotlinx.coroutines.DEBUG_PROPERTY_NAME
import kotlinx.coroutines.DEBUG_PROPERTY_VALUE_ON
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.di.importAllModules
import org.koin.core.context.startKoin

@Suppress("UNUSED")
private object Application {

    const val VERSION = "0.1.0"

    val logger: Logger = LogManager.getLogger(Application::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty(DEBUG_PROPERTY_NAME, DEBUG_PROPERTY_VALUE_ON)
        setProperty("version", VERSION)

        readBuildFile()

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
        setProperty("build.commit", build.getValue("git.commit.id.abbrev"))
        setProperty("build.message", build.getValue("git.commit.message.short"))
        setProperty("build.time", build.getValue("git.commit.time"))
        setProperty("build.branch", build.getOrDefault("git.branch", ""))
        setProperty("build.remote", build.getValue("git.remote.origin.url"))
    }

    private fun setProperty(key: String, value: String) {
        System.setProperty("org.katan.$key", value)
    }

}
