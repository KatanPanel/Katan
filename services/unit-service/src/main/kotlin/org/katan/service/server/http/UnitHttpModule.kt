package org.katan.service.server.http

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import org.katan.http.HttpModule
import org.katan.http.HttpModuleRegistry
import org.katan.service.server.http.routes.createUnit
import org.katan.service.server.http.routes.findUnit

internal class UnitHttpModule(registry: HttpModuleRegistry) : HttpModule(registry) {

    override fun install(app: Application) {
        app.routing {
            findUnit()
            createUnit()
        }
    }
}
