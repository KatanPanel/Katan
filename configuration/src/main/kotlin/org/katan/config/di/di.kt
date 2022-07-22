package org.katan.config.di

import org.katan.config.DefaultConfigLoader
import org.koin.core.module.Module
import org.koin.dsl.module

val configDI: Module = module {
    single { DefaultConfigLoader.load() }
}