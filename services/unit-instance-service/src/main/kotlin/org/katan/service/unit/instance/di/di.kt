package org.katan.service.unit.instance.di

import org.katan.service.unit.instance.repository.UnitInstanceRepository
import org.katan.service.unit.instance.repository.UnitInstanceRepositorySqlImpl
import org.koin.core.module.Module
import org.koin.dsl.module

public val unitInstanceServiceDI: Module = module {
    single<UnitInstanceRepository> { UnitInstanceRepositorySqlImpl() }
}