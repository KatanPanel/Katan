package org.katan.http.server.di

import org.katan.http.HttpModuleRegistry
import org.katan.http.routes.httpRoutesDI
import org.koin.core.module.Module
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module

val httpServerDI: Module = module {
    single { HttpModuleRegistry() } withOptions {
        createdAtStart()
    }
    includes(httpRoutesDI)
}