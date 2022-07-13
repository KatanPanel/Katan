package org.katan.config.di

import org.katan.config.DefaultConfigLoader
import org.koin.dsl.module

val ConfigModule: org.koin.core.module.Module = module {
    single { DefaultConfigLoader.load() }
}