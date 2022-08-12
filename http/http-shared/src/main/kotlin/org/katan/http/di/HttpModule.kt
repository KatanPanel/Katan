package org.katan.http.di

import io.ktor.server.application.Application

abstract class HttpModule(registry: HttpModuleRegistry) {

    init {
        @Suppress("LeakingThis")
        registry.register(this)
    }

    abstract fun install(app: Application)
}
