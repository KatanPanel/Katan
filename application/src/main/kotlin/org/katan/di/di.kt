package org.katan.di

import org.katan.config.di.ConfigModule
import org.katan.http.server.di.HttpModule
import org.katan.runtime.di.RuntimeModule
import org.katan.service.account.di.AccountServiceModule
import org.katan.service.id.di.IdServiceModule
import org.katan.service.network.di.NetworkServiceDi
import org.katan.service.server.di.UnitServiceModule
import org.katan.service.unit.instance.docker.di.DockerUnitInstanceServiceModule
import org.koin.core.KoinApplication
import org.koin.dsl.module

private val ServicesModule
    get() = module {
        includes(
            IdServiceModule,
            NetworkServiceDi,
            AccountServiceModule,
            UnitServiceModule,
            DockerUnitInstanceServiceModule
        )
    }

internal fun KoinApplication.importAllModules() {
    modules(ConfigModule, HttpModule, RuntimeModule, ServicesModule)
}