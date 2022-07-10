package org.katan.service.id

import org.koin.dsl.module
import org.koin.core.module.Module

public val IdServiceModule: Module = module {
    single<IdService> { SnowflakeIdServiceImpl() }
}