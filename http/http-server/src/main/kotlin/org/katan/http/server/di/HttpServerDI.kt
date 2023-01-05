package org.katan.http.server.di

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.katan.http.HttpModuleRegistry
import org.katan.http.websocket.WebSocketManager
import org.koin.core.module.Module
import org.koin.dsl.module

val httpServerDI: Module = module {
    single { HttpModuleRegistry() }
    single { WebSocketManager() }
    single<Validator> { Validation.buildDefaultValidatorFactory().validator }
}
