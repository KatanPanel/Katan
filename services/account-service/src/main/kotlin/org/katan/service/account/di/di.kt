package org.katan.service.account.di

import org.katan.service.account.AccountService
import org.katan.service.account.AccountServiceImpl
import org.koin.core.module.Module
import org.koin.dsl.module

public val AccountServiceModule: Module = module {
    single<AccountService> { AccountServiceImpl(get()) }
}