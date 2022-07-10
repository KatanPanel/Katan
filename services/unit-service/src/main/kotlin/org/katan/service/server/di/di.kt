package org.katan.service.server.di

import org.katan.service.server.DefaultUnitFactory
import org.katan.service.server.LocalUnitServiceImpl
import org.katan.service.server.UnitFactory
import org.katan.service.server.UnitService
import org.koin.core.module.Module
import org.koin.dsl.module

public val UnitServiceModule: Module = module {
    single<UnitFactory> { DefaultUnitFactory(get()) }
    single<UnitService> { LocalUnitServiceImpl(get()) }
}