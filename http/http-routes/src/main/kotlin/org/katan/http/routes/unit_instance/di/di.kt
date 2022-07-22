package org.katan.http.routes.unit_instance.di

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import org.katan.http.HttpModule
import org.katan.http.HttpModuleRegistry
import org.katan.http.routes.unit_instance.routes.killUnitInstance
import org.katan.http.routes.unit_instance.routes.startUnitInstance
import org.katan.http.routes.unit_instance.routes.stopUnitInstance
import org.koin.core.module.Module
import org.koin.core.module.dsl.createdAtStart
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module

internal val unitInstanceHttpRoutesDI: Module = module {
    single<HttpModule> { UnitInstanceRoutesHttpModule(get()) } withOptions {
        createdAtStart()
    }
}

private class UnitInstanceRoutesHttpModule(registry: HttpModuleRegistry) : HttpModule(registry) {

    override fun install(app: Application) {
        app.routing {
            startUnitInstance()
            stopUnitInstance()
            killUnitInstance()
        }
    }

}