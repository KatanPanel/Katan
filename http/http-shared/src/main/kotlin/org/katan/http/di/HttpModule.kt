package org.katan.http.di

import io.ktor.server.application.Application
import org.katan.http.di.HttpModuleRegistry

abstract class HttpModule(registry: HttpModuleRegistry) {

    init {
        @Suppress("LeakingThis")
        registry.register(this)
    }

    abstract fun install(app: Application)
}
