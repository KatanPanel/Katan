package org.katan.service.server.di

import org.katan.http.HttpModule
import org.katan.service.server.LocalUnitServiceImpl
import org.katan.service.server.UnitService
import org.katan.service.server.http.UnitHttpModule
import org.koin.core.module.Module
import org.koin.dsl.module

public val unitServiceDI: Module = module {
    single<UnitService> { LocalUnitServiceImpl(get(), get(), get()) }
    single<HttpModule>(createdAtStart = true) { UnitHttpModule(get()) }
}
