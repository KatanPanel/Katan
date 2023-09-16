package org.katan

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.DEBUG_PROPERTY_NAME
import kotlinx.coroutines.DEBUG_PROPERTY_VALUE_AUTO
import kotlinx.coroutines.DEBUG_PROPERTY_VALUE_ON
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Suppress("UNUSED")
private object Application {

    val logger: Logger = LogManager.getLogger(Application::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        val di = Katan.createDI()
        val isDevMode = di.koin.get<KatanConfig>().isDevelopment

        System.setProperty("io.ktor.development", isDevMode.toString())
        System.setProperty(
            DEBUG_PROPERTY_NAME,
            if (isDevMode) {
                DEBUG_PROPERTY_VALUE_ON
            } else {
                DEBUG_PROPERTY_VALUE_AUTO
            },
        )
        start()
    }

    private fun start() {
        val katan = Katan()
        Runtime.getRuntime().addShutdownHook(
            Thread {
                runBlocking { katan.close() }
            },
        )
        runBlocking(CoroutineName("Katan")) {
            katan.start()
        }
    }
}
