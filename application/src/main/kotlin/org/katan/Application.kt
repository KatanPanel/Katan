package org.katan

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.di.importAllModules
import org.koin.core.context.startKoin

@Suppress("UNUSED")
private object Application {

    val LOGGER: Logger = LogManager.getLogger(Application::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
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
}