package org.katan.service.network.di

import org.katan.service.network.NetworkService
import org.katan.service.network.NetworkServiceImpl
import org.koin.core.module.Module
import org.koin.dsl.module

public val NetworkServiceDi: Module = module {
    single<NetworkService> { NetworkServiceImpl() }
}