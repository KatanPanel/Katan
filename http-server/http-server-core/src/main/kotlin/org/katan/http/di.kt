package org.katan.http

import org.koin.core.module.Module
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module

val HttpModule: Module = module {
    single { HttpModuleRegistry() } withOptions {
        createdAtStart()
    }
}