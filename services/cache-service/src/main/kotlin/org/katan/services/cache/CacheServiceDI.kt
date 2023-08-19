package org.katan.services.cache

import org.koin.core.module.Module
import org.koin.dsl.module

public val cacheServiceDI: Module = module {
    single<CacheService> { RedisCacheService(config = get()) }
}
