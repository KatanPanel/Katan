package org.katan.service.server

import org.koin.core.module.Module
import org.koin.dsl.module

public val ServerServiceModule: Module = module {
    single<ServerFactory> { DefaultServerFactory(get()) }
    single<ServerService> { ServerServiceMock(get()) }
}