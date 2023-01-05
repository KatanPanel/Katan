package org.katan.service.blueprint.di

import org.katan.http.importHttpModule
import org.katan.service.blueprint.BlueprintService
import org.katan.service.blueprint.BlueprintServiceImpl
import org.katan.service.blueprint.http.BlueprintHttpModule
import org.katan.service.blueprint.provider.BlueprintResourceProviderRegistry
import org.katan.service.blueprint.repository.BlueprintRepository
import org.katan.service.blueprint.repository.BlueprintRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val blueprintServiceDI: Module = module {
    importHttpModule(::BlueprintHttpModule)
    single<BlueprintRepository> { BlueprintRepositoryImpl(database = get()) }
    single { BlueprintResourceProviderRegistry() }
    single<BlueprintService> {
        BlueprintServiceImpl(
            idService = get(),
            instanceService = get(),
            httpClient = get(),
            repository = get(),
            providerRegistry = get(),
            fsService = get()
        )
    }
}
