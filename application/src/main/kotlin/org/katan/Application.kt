package org.katan

import kotlinx.coroutines.DEBUG_PROPERTY_NAME
import kotlinx.coroutines.DEBUG_PROPERTY_VALUE_ON
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.di.importAllModules
import org.koin.core.context.startKoin

@Suppress("UNUSED")
private object Application {

    val logger: Logger = LogManager.getLogger(Application::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        System.setProperty(DEBUG_PROPERTY_NAME, DEBUG_PROPERTY_VALUE_ON)

        try {
            startKoin {
                logger(KoinLog4jLogger())
                importAllModules()
            }
        } catch (exception: Throwable) {
            logger.error("An error occurred during Katan initialization.", exception)
            return
        }

        runCatching {
            Katan().start()
        }.onFailure { exception ->
            logger.error("Failed to start Katan.", exception)
        }
    }
}
