package org.katan.di

import org.katan.http.server.di.HttpModule
import org.katan.runtime.di.RuntimeModule
import org.katan.service.container.ContainerServiceModule
import org.katan.service.id.IdServiceModule
import org.katan.service.server.di.ServiceServerModule
import org.koin.core.KoinApplication
import org.koin.dsl.module

private val ServicesModule
    get() = module {
        includes(
            IdServiceModule,
            ServiceServerModule,
            ContainerServiceModule
        )
    }

internal fun KoinApplication.importAllModules() {
    modules(HttpModule, RuntimeModule, ServicesModule)
}