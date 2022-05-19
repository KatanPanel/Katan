package org.katan.http

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.ApplicationEngineEnvironment
import io.ktor.server.engine.ApplicationEngineEnvironmentBuilder
import io.ktor.server.engine.addShutdownHook
import io.ktor.server.engine.applicationEngineEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.resources.Resources
import io.ktor.server.routing.Routing
import org.katan.ServerModule

private typealias ApplicationBlock = Application.() -> Unit

class HttpServer(
    val host: String? = null,
    val port: Int
) {

    init {
        System.setProperty("io.ktor.development", "true")
    }

    private val engine: ApplicationEngine = embeddedServer(Netty, setupEngine())

    private fun setupEngine(): ApplicationEngineEnvironment {
        val httpServer = this
        return applicationEngineEnvironment {
            registerModules()

            connector {
                host = httpServer.host ?: "0.0.0.0"
                port = httpServer.port
            }
        }
    }

    private fun ApplicationEngineEnvironmentBuilder.registerModules() {
        val before: ApplicationBlock = { installDefaultFeatures() }
        val modules: Array<ApplicationBlock> = arrayOf({ ServerModule(before) })

        module {
            modules.forEach {
                it.invoke(this@module)
            }
        }
    }

    private fun Application.installDefaultFeatures() {
        install(Routing)
        install(Resources)
        install(DefaultHeaders)
        install(AutoHeadResponse)
        install(CallLogging)
    }

    fun start() {
        engine.start(wait = true)
        engine.addShutdownHook {
            stop()
        }
    }

    fun stop() {
        engine.stop(
            gracePeriodMillis = 1000,
            timeoutMillis = 5000
        )
    }

}