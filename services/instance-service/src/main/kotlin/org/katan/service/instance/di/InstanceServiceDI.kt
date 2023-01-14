package org.katan.service.instance.di

import org.katan.http.importHttpModule
import org.katan.service.instance.DockerInstanceServiceImpl
import org.katan.service.instance.InstanceService
import org.katan.service.instance.http.UnitInstanceHttpModule
import org.katan.service.instance.repository.UnitInstanceRepository
import org.katan.service.instance.repository.UnitInstanceRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val instanceServiceDI: Module = module {
    importHttpModule(::UnitInstanceHttpModule)
    single<UnitInstanceRepository> {
        UnitInstanceRepositoryImpl(
            database = get()
        )
    }
    single<InstanceService> {
        DockerInstanceServiceImpl(
            eventsDispatcher = get(),
            idService = get(),
            networkService = get(),
            blueprintService = get(),
            dockerClient = get(),
            unitInstanceRepository = get(),
            config = get()
        )
    }
}
