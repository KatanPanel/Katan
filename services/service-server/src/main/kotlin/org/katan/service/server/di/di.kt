package org.katan.service.server.di

import io.ktor.server.routing.Routing
import org.katan.service.server.DefaultServerFactory
import org.katan.service.server.ServerFactory
import org.katan.service.server.ServerService
import org.katan.service.server.ServerServiceMock
import org.koin.core.module.Module
import org.koin.dsl.module

public val ServerServiceModule: Module = module {
    single<ServerFactory> { DefaultServerFactory(get()) }
    single<ServerService> { ServerServiceMock(get()) }
    single<Routing> { routing {} }
}