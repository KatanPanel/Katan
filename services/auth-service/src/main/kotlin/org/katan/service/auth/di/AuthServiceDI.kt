package org.katan.service.auth.di

import org.katan.service.auth.AuthService
import org.katan.service.auth.JWTAuthServiceImpl
import org.katan.service.auth.http.AuthHttpModule
import org.koin.dsl.module

public val authServiceDI: org.koin.core.module.Module = module {
    single<AuthService> { JWTAuthServiceImpl(get(), get()) }
    single(createdAtStart = true) { AuthHttpModule(get()) }
}
