package org.katan.service.blueprint.di

import org.katan.http.importHttpModule
import org.katan.service.blueprint.BlueprintService
import org.katan.service.blueprint.BlueprintServiceImpl
import org.katan.service.blueprint.http.BlueprintHttpModule
import org.katan.service.blueprint.provider.BlueprintSpecProvider
import org.katan.service.blueprint.provider.CombinedBlueprintSpecProvider
import org.katan.service.blueprint.provider.GithubBlueprintSpecProvider
import org.katan.service.blueprint.repository.BlueprintRepository
import org.katan.service.blueprint.repository.BlueprintRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val blueprintServiceDI: Module = module {
    importHttpModule(::BlueprintHttpModule)
    single<BlueprintRepository> { BlueprintRepositoryImpl(database = get()) }
    single<BlueprintSpecProvider> {
        CombinedBlueprintSpecProvider(
            listOf(
                GithubBlueprintSpecProvider(httpClient = get())
            )
        )
    }
    single<BlueprintService> {
        BlueprintServiceImpl(
            idService = get(),
            blueprintRepository = get(),
            blueprintSpecProvider = get(),
            fsService = get()
        )
    }
}
