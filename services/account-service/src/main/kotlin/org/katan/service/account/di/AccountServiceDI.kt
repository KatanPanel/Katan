package org.katan.service.account.di

import org.katan.security.Hash
import org.katan.http.importHttpModule
import org.katan.service.account.AccountService
import org.katan.service.account.AccountServiceImpl
import org.katan.service.account.http.AccountHttpModule
import org.katan.service.account.repository.AccountsRepository
import org.katan.service.account.repository.AccountsRepositoryImpl
import org.koin.core.module.Module
import org.koin.dsl.module

val accountServiceDI: Module = module {
    importHttpModule(::AccountHttpModule)
    single<AccountsRepository> {
        AccountsRepositoryImpl(database = get())
    }
    single<AccountService> {
        AccountServiceImpl(
            idService = get(),
            accountsRepository = get(),
            hashAlgorithm = Hash.Bcrypt,
            eventsDispatcher = get()
        )
    }
}
