package org.katan.service.unit.instance.di

import org.katan.http.di.HttpModule
import org.katan.service.unit.instance.http.UnitInstanceHttpModule
import org.koin.core.module.Module
import org.koin.dsl.module

public val unitInstanceServiceDI: Module = module {
    single<HttpModule>(createdAtStart = true) { UnitInstanceHttpModule(get()) }
}
