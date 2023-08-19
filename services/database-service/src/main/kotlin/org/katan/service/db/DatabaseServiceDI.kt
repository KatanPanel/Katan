package org.katan.service.db

import org.koin.core.module.Module
import org.koin.dsl.module

public val databaseServiceDI: Module = module {
    single { PostgresDatabaseService(config = get()).get() }
}
