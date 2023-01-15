package org.katan.service.instance.di

import org.katan.http.importHttpModule
import org.katan.service.instance.DockerInstanceServiceImpl
import org.katan.service.instance.InstanceService
import org.katan.service.instance.http.InstanceHttpModule
import org.katan.service.instance.repository.InstanceRepository
import org.katan.service.instance.repository.InstanceRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val instanceServiceDI: Module = module {
    importHttpModule(::InstanceHttpModule)
    single<InstanceRepository> {
        InstanceRepositoryImpl(
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
            instanceRepository = get(),
            config = get()
        )
    }
}
