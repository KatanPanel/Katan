package org.katan.service.auth.di

import org.katan.model.security.Hash
import org.katan.service.auth.AuthService
import org.katan.service.auth.AuthServiceImpl
import org.katan.service.auth.crypto.BcryptHash
import org.katan.service.auth.http.AuthHttpModule
import org.koin.dsl.module

public val authServiceDI: org.koin.core.module.Module = module {
    single<Hash> { BcryptHash() }
    single<AuthService> { AuthServiceImpl(get()) }
    single(createdAtStart = true) { AuthHttpModule(get()) }
}