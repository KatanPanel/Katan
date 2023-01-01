package org.katan.service.dockerInstance.di

import org.katan.service.dockerInstance.DockerInstanceServiceImpl
import org.katan.service.dockerInstance.repository.UnitInstanceRepositoryImpl
import org.katan.service.instance.InstanceService
import org.katan.service.instance.repository.UnitInstanceRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val dockerInstanceServiceImplDI: Module = module {
    single<UnitInstanceRepository> { UnitInstanceRepositoryImpl(get()) }
    single<InstanceService>(createdAtStart = true) {
        DockerInstanceServiceImpl(get(), get(), get(), get(), get(), get())
    }
}
