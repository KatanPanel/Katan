package org.katan.service.server.di

import org.katan.http.di.HttpModule
import org.katan.service.server.LocalUnitServiceImpl
import org.katan.service.server.UnitService
import org.katan.service.server.http.UnitHttpModule
import org.katan.service.server.repository.UnitRepository
import org.katan.service.server.repository.UnitRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

public val unitServiceDI: Module = module {
    single<UnitRepository> {
        UnitRepositoryImpl(database = get())
    }
    single<UnitService> {
        LocalUnitServiceImpl(
            config = get(),
            idService = get(),
            unitInstanceService = get(),
            unitRepository = get(),
            accountService = get()
        )
    }
    single<HttpModule>(createdAtStart = true) {
        UnitHttpModule(registry = get())
    }
}
