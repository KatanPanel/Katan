package org.katan.service.unit.di

import org.katan.http.importHttpModule
import org.katan.service.unit.LocalUnitServiceImpl
import org.katan.service.unit.UnitService
import org.katan.service.unit.http.UnitHttpModule
import org.katan.service.unit.repository.UnitRepository
import org.katan.service.unit.repository.UnitRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val unitServiceDI: Module = module {
    importHttpModule(::UnitHttpModule)
    single<UnitRepository> {
        UnitRepositoryImpl(database = get())
    }
    single<UnitService> {
        LocalUnitServiceImpl(
            config = get(),
            idService = get(),
            instanceService = get(),
            unitRepository = get(),
            accountService = get(),
            blueprintService = get()
        )
    }
}
