package org.katan.service.db.di

import org.katan.service.db.PostgreSQLDatabaseServiceImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val databaseServiceDI: Module = module {
    single { PostgreSQLDatabaseServiceImpl(get()).get() }
}
