package org.katan.model

import org.koin.core.module.Module
import org.koin.dsl.module

public val modelDI: Module = module {
    single { KatanConfig() }
}
