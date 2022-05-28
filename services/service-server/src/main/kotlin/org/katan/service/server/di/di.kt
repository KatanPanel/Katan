package org.katan.service.server.di

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import org.katan.http.HttpModule
import org.katan.http.HttpModuleRegistry
import org.katan.service.server.DefaultServerFactory
import org.katan.service.server.ServerFactory
import org.katan.service.server.ServerService
import org.katan.service.server.ServerServiceMock
import org.katan.service.server.http.routes.createServer
import org.katan.service.server.http.routes.findServer
import org.koin.core.module.Module
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module

public val ServerServiceModule: Module = module {
    single<ServerFactory> { DefaultServerFactory(get()) }
    single<ServerService> { ServerServiceMock(get()) }
    single<HttpModule> { ServerHttpModule(get()) } withOptions {
        createdAtStart()
    }
}

private class ServerHttpModule(
    registry: HttpModuleRegistry
) : HttpModule {

    init {
        registry.register(this)
    }

    override fun install(app: Application) {
        app.apply {
            routing {
                findServer()
                createServer()
            }
        }
    }

}