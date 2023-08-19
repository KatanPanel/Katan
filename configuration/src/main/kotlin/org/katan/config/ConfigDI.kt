package org.katan.config

import org.koin.core.module.Module
import org.koin.dsl.module

public val configDI: Module = module {
    single { KatanConfig() }
}
