package org.katan.http.server.di

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.katan.http.di.HttpModuleRegistry
import org.katan.http.websocket.WebSocketManager
import org.koin.core.module.Module
import org.koin.dsl.module

val httpServerDI: Module = module {
    single(createdAtStart = true) { HttpModuleRegistry() }
    single<Validator> { Validation.buildDefaultValidatorFactory().validator }
    single { WebSocketManager() }
}
