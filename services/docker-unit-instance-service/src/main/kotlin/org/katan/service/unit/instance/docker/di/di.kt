package org.katan.service.unit.instance.docker.di

import org.katan.service.unit.instance.UnitInstanceService
import org.katan.service.unit.instance.docker.DockerUnitInstanceServiceImpl
import org.koin.core.module.Module
import org.koin.dsl.module

public val DockerUnitInstanceServiceModule: Module = module {
    single<UnitInstanceService> { DockerUnitInstanceServiceImpl(get(), get()) }
}