package org.katan.service.account.di

import org.katan.service.account.AccountService
import org.katan.service.account.LocalAccountServiceImpl
import org.koin.core.module.Module
import org.koin.dsl.module

public val accountServiceDI: Module = module {
    single<AccountService> { LocalAccountServiceImpl(get()) }
}