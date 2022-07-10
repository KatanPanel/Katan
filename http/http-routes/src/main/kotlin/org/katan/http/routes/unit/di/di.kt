package org.katan.http.routes.unit.di

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import org.katan.http.HttpModule
import org.katan.http.HttpModuleRegistry
import org.katan.http.routes.unit.routes.createUnit
import org.katan.http.routes.unit.routes.findServer
import org.katan.http.routes.unit.routes.listServers
import org.koin.core.module.Module
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module

internal val UnitRoutesHttpDI: Module = module {
    single<HttpModule> { UnitRoutesHttpModule(get()) } withOptions {
        createdAtStart()
    }
}

private class UnitRoutesHttpModule(registry: HttpModuleRegistry) : HttpModule(registry) {

    override fun install(app: Application) {
        app.routing {
            listServers()
            findServer()
            createUnit()
        }
    }

}