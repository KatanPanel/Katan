package org.katan

import kotlinx.serialization.json.Json
import me.devnatan.yoki.Yoki
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.katan.http.server.HttpServer
import org.katan.http.server.httpServerDI
import org.katan.service.account.di.accountServiceDI
import org.katan.service.auth.authServiceDI
import org.katan.service.blueprint.blueprintServiceDI
import org.katan.service.db.databaseServiceDI
import org.katan.service.fs.host.hostFsServiceDI
import org.katan.service.id.idServiceDI
import org.katan.service.instance.instanceServiceDI
import org.katan.service.network.networkServiceDI
import org.katan.service.projects.projectServiceDI
import org.katan.service.unit.unitServiceDI
import org.katan.services.cache.cacheServiceDI
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.sql.SQLException
import kotlin.system.exitProcess

internal class Katan : KoinComponent {

    private val config: KatanConfig by inject()
    private lateinit var httpServer: HttpServer

    suspend fun start() {
        checkDatabaseConnection()
        httpServer = HttpServer(config.host, config.port)
        httpServer.start()
    }

    private suspend fun checkDatabaseConnection() {
        val database = get<Database>()
        try {
            newSuspendedTransaction(db = database) {
                database.connector()
            }
        } catch (exception: SQLException) {
            if (config.isDevelopment) {
                logger.error("Unable to establish database connection.", exception)
            } else {
                logger.debug("Unable to establish database connection: {}", exception.message)
            }

            exitProcess(0)
        }
    }

    internal fun close() {
        if (!::httpServer.isInitialized) {
            return
        }

        httpServer.stop()
    }

    internal companion object {

        private val logger: Logger = LogManager.getLogger(Katan::class.java)

        internal fun createDI(): KoinApplication = startKoin {
            logger(KoinLog4jLogger())

            val rootModule = module {
                single {
                    val config = get<KatanConfig>()
                    Yoki {
                        socketPath(config.dockerHost)
                    }
                }
                single {
                    Json {
                        ignoreUnknownKeys = true
                    }
                }
            }

            modules(
                rootModule,
                httpServerDI,
                coreDI,
                idServiceDI,
                accountServiceDI,
                unitServiceDI,
                networkServiceDI,
                instanceServiceDI,
                cacheServiceDI,
                databaseServiceDI,
                hostFsServiceDI,
                blueprintServiceDI,
                authServiceDI,
                projectServiceDI,
            )
        }
    }
}
