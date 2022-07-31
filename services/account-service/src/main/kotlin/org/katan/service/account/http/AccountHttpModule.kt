package org.katan.service.account.http

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import org.katan.http.HttpModule
import org.katan.http.HttpModuleRegistry
import org.katan.service.account.http.routes.register

internal class AccountHttpModule(registry: HttpModuleRegistry) : HttpModule(registry) {

    override fun install(app: Application) {
        app.routing {
            register()
        }
    }
}
