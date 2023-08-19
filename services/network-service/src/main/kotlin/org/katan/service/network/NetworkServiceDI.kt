package org.katan.service.network

import org.katan.service.network.DockerNetworkServiceImpl
import org.katan.service.network.NetworkService
import org.koin.core.module.Module
import org.koin.dsl.module

public val networkServiceDI: Module = module {
    single<NetworkService> {
        DockerNetworkServiceImpl(
            dockerClient = get()
        )
    }
}
