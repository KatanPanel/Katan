package org.katan.service.auth.di

import org.katan.service.auth.AuthService
import org.katan.service.auth.AuthServiceImpl
import org.koin.dsl.module

public val authServiceDI: org.koin.core.module.Module = module {
    single<AuthService> { AuthServiceImpl(get()) }
}