package org.katan.service.id.di

import org.katan.service.id.IdService
import org.katan.service.id.SnowflakeIdServiceImpl
import org.koin.dsl.module
import org.koin.core.module.Module

public val IdServiceModule: Module = module {
    single<IdService> { SnowflakeIdServiceImpl(get()) }
}