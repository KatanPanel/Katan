package org.katan.service.auth.di

import com.auth0.jwt.interfaces.JWTVerifier
import org.katan.http.importHttpModule
import org.katan.service.auth.AuthService
import org.katan.service.auth.JWTAuthServiceImpl
import org.katan.service.auth.JWTVerifierImpl
import org.katan.service.auth.http.AuthHttpModule
import org.koin.core.module.Module
import org.koin.dsl.module

val authServiceDI: Module = module {
    importHttpModule(::AuthHttpModule)
    single<AuthService> {
        JWTAuthServiceImpl(
            accountService = get(),
            saltedHash = get()
        )
    }
    single<JWTVerifier> {
        JWTVerifierImpl()
    }
}
