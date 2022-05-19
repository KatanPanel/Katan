package org.katan.service.container.di

import org.katan.service.container.ContainerDiscoveryService
import org.katan.service.container.ContainerDiscoveryServiceImpl
import org.katan.service.container.ContainerFactory
import org.katan.service.container.ContainerService
import org.katan.service.container.ContainerServiceImpl
import org.katan.service.container.DefaultContainerFactory
import org.koin.dsl.module
import org.koin.core.module.Module

public val ContainerServiceModule: Module = module {
    single<ContainerDiscoveryService> { ContainerDiscoveryServiceImpl() }
    single<ContainerFactory> { DefaultContainerFactory() }
    single<ContainerService> { ContainerServiceImpl(get(), get()) }
}