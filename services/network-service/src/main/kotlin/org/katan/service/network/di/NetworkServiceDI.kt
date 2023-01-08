package org.katan.service.network.di

import org.katan.service.network.DockerNetworkServiceImpl
import org.katan.service.network.NetworkService
import org.koin.core.module.Module
import org.koin.dsl.module

val networkServiceDI: Module = module {
    single<NetworkService> {
        DockerNetworkServiceImpl(
            dockerClient = get()
        )
    }
}
