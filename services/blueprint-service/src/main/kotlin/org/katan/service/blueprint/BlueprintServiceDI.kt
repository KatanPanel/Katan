package org.katan.service.blueprint

import org.katan.http.importHttpModule
import org.katan.service.blueprint.http.BlueprintHttpModule
import org.katan.service.blueprint.parser.BlueprintParser
import org.katan.service.blueprint.provider.BlueprintSpecProvider
import org.katan.service.blueprint.provider.CombinedBlueprintSpecProvider
import org.katan.service.blueprint.provider.RemoteBlueprintSpecProvider
import org.katan.service.blueprint.repository.BlueprintRepository
import org.katan.service.blueprint.repository.BlueprintRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

public val blueprintServiceDI: Module = module {
    importHttpModule(::BlueprintHttpModule)
    single<BlueprintRepository> { BlueprintRepositoryImpl(database = get()) }
    single<BlueprintSpecProvider> {
        CombinedBlueprintSpecProvider(
            listOf(
                RemoteBlueprintSpecProvider(
                    httpClient = get(),
                    parser = get()
                )
            )
        )
    }
    single<BlueprintService> {
        BlueprintServiceImpl(
            idService = get(),
            blueprintRepository = get(),
            blueprintSpecProvider = get()
        )
    }
    single {
        BlueprintParser()
    }
}
