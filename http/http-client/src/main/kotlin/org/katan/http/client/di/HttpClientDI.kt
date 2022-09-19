package org.katan.http.client.di

import org.katan.http.client.createHttpClient
import org.koin.core.module.Module
import org.koin.dsl.module

val httpClientDI: Module = module {
    single { createHttpClient() }
}
