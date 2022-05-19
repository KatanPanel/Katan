package org.katan.di

import org.katan.ContainerDiscoveryService
import org.katan.ContainerDiscoveryServiceImpl
import org.katan.ContainerFactory
import org.katan.ContainerService
import org.katan.ContainerServiceImpl
import org.katan.DefaultContainerFactory
import org.koin.dsl.module
import org.koin.core.module.Module

public val ContainerServiceModule: Module = module {
    single<ContainerDiscoveryService> { ContainerDiscoveryServiceImpl() }
    single<ContainerFactory> { DefaultContainerFactory() }
    single<ContainerService> { ContainerServiceImpl(get(), get()) }
}