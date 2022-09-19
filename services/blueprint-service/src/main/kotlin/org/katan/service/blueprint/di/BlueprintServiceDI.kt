package org.katan.service.blueprint.di

import org.katan.service.blueprint.BlueprintService
import org.katan.service.blueprint.BlueprintServiceImpl
import org.katan.service.blueprint.http.BlueprintHttpModule
import org.katan.service.blueprint.provider.BlueprintResourceProviderRegistry
import org.katan.service.blueprint.repository.BlueprintRepository
import org.katan.service.blueprint.repository.BlueprintRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val blueprintServiceDI: Module = module {
    single<BlueprintRepository> { BlueprintRepositoryImpl(get()) }
    single { BlueprintResourceProviderRegistry() }
    single<BlueprintService> { BlueprintServiceImpl(get(), get(), get(), get(), get(), get()) }
    single(createdAtStart = true) { BlueprintHttpModule(get()) }
}
