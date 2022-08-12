package org.katan.service.account.di

import org.katan.http.di.HttpModule
import org.katan.service.account.AccountServerImpl
import org.katan.service.account.AccountService
import org.katan.service.account.http.AccountHttpModule
import org.katan.service.account.repository.AccountsRepository
import org.katan.service.account.repository.AccountsRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

public val accountServiceDI: Module = module {
    single<AccountsRepository> { AccountsRepositoryImpl(get()) }
    single<AccountService> { AccountServerImpl(get(), get(), get()) }
    single<HttpModule>(createdAtStart = true) { AccountHttpModule(get()) }
}
