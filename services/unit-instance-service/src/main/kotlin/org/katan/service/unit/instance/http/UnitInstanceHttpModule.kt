package org.katan.service.unit.instance.http

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.routing
import org.katan.http.di.HttpModule
import org.katan.http.di.HttpModuleRegistry
import org.katan.service.unit.instance.http.routes.updateStatus

internal class UnitInstanceHttpModule(registry: HttpModuleRegistry) : HttpModule(registry) {

    override fun install(app: Application) {
        app.routing {
            authenticate {
                updateStatus()
            }
        }
    }
}
