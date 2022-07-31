package org.katan.http

import io.ktor.server.application.Application

abstract class HttpModule(registry: HttpModuleRegistry) {

    init {
        @Suppress("LeakingThis")
        registry.register(this)
    }

    abstract fun install(app: Application)
}
