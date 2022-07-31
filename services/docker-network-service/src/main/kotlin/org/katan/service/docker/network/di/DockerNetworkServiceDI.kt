package org.katan.service.docker.network.di

import org.katan.service.docker.network.DockerNetworkServiceImpl
import org.katan.service.network.NetworkService
import org.koin.core.module.Module
import org.koin.dsl.module

public val dockerNetworkServiceDI: Module = module {
    single<NetworkService> { DockerNetworkServiceImpl(get()) }
}
