package org.katan.service.id

import org.koin.core.module.Module
import org.koin.dsl.module

public val idServiceDI: Module = module {
    single<IdService> {
        SnowflakeIdServiceImpl(config = get())
    }
}
