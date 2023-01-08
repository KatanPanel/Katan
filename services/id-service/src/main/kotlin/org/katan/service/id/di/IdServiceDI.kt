package org.katan.service.id.di

import org.katan.service.id.IdService
import org.katan.service.id.SnowflakeIdServiceImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val idServiceDI: Module = module {
    single<IdService> {
        SnowflakeIdServiceImpl(config = get())
    }
}
