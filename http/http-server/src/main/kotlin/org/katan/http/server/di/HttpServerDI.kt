package org.katan.http.server.di

import org.katan.http.HttpModuleRegistry
import org.koin.core.module.Module
import org.koin.dsl.module

val httpServerDI: Module = module {
    single(createdAtStart = true) { HttpModuleRegistry() }
}
