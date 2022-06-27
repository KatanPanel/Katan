package org.katan.http.routes.server.di

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import org.katan.http.HttpModule
import org.katan.http.HttpModuleRegistry
import org.katan.http.routes.server.routes.createServer
import org.katan.http.routes.server.routes.findServer
import org.koin.core.module.Module
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module

val HttpRoutesServerModule: Module = module {
    single<HttpModule> { HttpRoutesServerModuleHttpModule(get()) } withOptions {
        createdAtStart()
    }
}

private class HttpRoutesServerModuleHttpModule(registry: HttpModuleRegistry) : HttpModule(registry) {

    override fun install(app: Application) {
        app.routing {
            findServer()
            createServer()
        }
    }

}