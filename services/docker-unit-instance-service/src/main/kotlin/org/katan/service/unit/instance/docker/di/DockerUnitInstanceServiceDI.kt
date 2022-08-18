package org.katan.service.unit.instance.docker.di

import org.katan.service.unit.instance.UnitInstanceService
import org.katan.service.unit.instance.docker.DockerUnitInstanceServiceImpl
import org.katan.service.unit.instance.docker.repository.UnitInstanceRepositoryImpl
import org.katan.service.unit.instance.repository.UnitInstanceRepository
import org.koin.core.module.Module
import org.koin.dsl.module

public val dockerUnitInstanceServiceImplDI: Module = module {
    single<UnitInstanceRepository> { UnitInstanceRepositoryImpl(get()) }
    single<UnitInstanceService>(createdAtStart = true) {
        DockerUnitInstanceServiceImpl(get(), get(), get(), get(), get(), get())
    }
}
