package org.katan.service.container

import org.koin.dsl.module
import org.koin.core.module.Module

public val ContainerServiceModule: Module = module {
    single<ContainerDiscoveryService> { ContainerDiscoveryServiceImpl() }
    single<ContainerFactory> { FakeContainerFactory() }
    single<ContainerService> { ContainerServiceImpl(get(), get()) }
}