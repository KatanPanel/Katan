package org.katan.config.di

import org.katan.config.KatanConfig
import org.koin.core.module.Module
import org.koin.dsl.module

val configDI: Module = module {
    single { KatanConfig() }
}
