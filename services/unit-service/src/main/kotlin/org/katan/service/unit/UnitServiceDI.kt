package org.katan.service.unit

import org.katan.http.importHttpModule
import org.katan.service.unit.http.UnitHttpModule
import org.katan.service.unit.repository.UnitRepository
import org.katan.service.unit.repository.UnitRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

public val unitServiceDI: Module = module {
    importHttpModule(::UnitHttpModule)
    single<UnitRepository> {
        UnitRepositoryImpl(database = get())
    }
    single<UnitService> {
        LocalUnitService(
            config = get(),
            idService = get(),
            instanceService = get(),
            unitRepository = get(),
            accountService = get(),
            eventsDispatcher = get()
        )
    }
}
