package org.katan.service.auth

import com.auth0.jwt.interfaces.JWTVerifier
import org.katan.http.importHttpModule
import org.katan.security.Hash
import org.katan.service.auth.http.AuthHttpModule
import org.koin.core.module.Module
import org.koin.dsl.module

public val authServiceDI: Module = module {
    importHttpModule(::AuthHttpModule)
    single<AuthService> {
        JWTAuthServiceImpl(
            accountService = get(),
            hashAlgorithm = Hash.Bcrypt
        )
    }
    single<JWTVerifier> {
        JWTVerifierImpl()
    }
}
