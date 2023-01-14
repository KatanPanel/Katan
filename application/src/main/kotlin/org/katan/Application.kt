package org.katan

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.DEBUG_PROPERTY_NAME
import kotlinx.coroutines.DEBUG_PROPERTY_VALUE_AUTO
import kotlinx.coroutines.DEBUG_PROPERTY_VALUE_ON
import kotlinx.coroutines.runBlocking
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.config.KatanConfig
import org.katan.config.di.configDI
import org.katan.crypto.di.cryptoDI
import org.katan.docker.di.dockerClientDI
import org.katan.event.di.eventsDispatcherDI
import org.katan.http.client.di.httpClientDI
import org.katan.http.server.di.httpServerDI
import org.katan.service.account.di.accountServiceDI
import org.katan.service.auth.di.authServiceDI
import org.katan.service.blueprint.di.blueprintServiceDI
import org.katan.service.db.di.databaseServiceDI
import org.katan.service.fs.host.di.hostFsServiceDI
import org.katan.service.id.di.idServiceDI
import org.katan.service.instance.di.instanceServiceDI
import org.katan.service.network.di.networkServiceDI
import org.katan.service.unit.di.unitServiceDI
import org.katan.services.cache.di.cacheServiceDI
import org.koin.core.context.startKoin
import kotlin.reflect.jvm.jvmName

@Suppress("UNUSED")
private object Application {

    val logger: Logger = LogManager.getLogger(Application::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        val di = startKoin {
            logger(KoinLog4jLogger())
            modules(
                configDI,
                cryptoDI,
                httpServerDI,
                eventsDispatcherDI,
                idServiceDI,
                authServiceDI,
                accountServiceDI,
                unitServiceDI,
                networkServiceDI,
                instanceServiceDI,
                cacheServiceDI,
                databaseServiceDI,
                dockerClientDI,
                hostFsServiceDI,
                httpClientDI,
                blueprintServiceDI
            )
        }

        // enables coroutines debug logs if it's in development mode
        System.setProperty(
            DEBUG_PROPERTY_NAME,
            if (di.koin.get<KatanConfig>().isDevelopment) {
                DEBUG_PROPERTY_VALUE_ON
            } else {
                DEBUG_PROPERTY_VALUE_AUTO
            }
        )

        val katan = Katan()

        Runtime.getRuntime().addShutdownHook(
            Thread {
                katan.close()
            }
        )

        runBlocking(CoroutineName(Katan::class.jvmName)) {
            katan.start()
        }
    }
}
