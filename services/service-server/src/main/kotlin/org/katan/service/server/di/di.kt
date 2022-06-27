package org.katan.service.server.di

import org.katan.service.server.DefaultServerFactory
import org.katan.service.server.InMemoryServerService
import org.katan.service.server.ServerFactory
import org.katan.service.server.ServerService
import org.koin.core.module.Module
import org.koin.dsl.module

public val ServiceServerModule: Module = module {
    single<ServerFactory> { DefaultServerFactory(get()) }
    single<ServerService> { InMemoryServerService(get()) }
}