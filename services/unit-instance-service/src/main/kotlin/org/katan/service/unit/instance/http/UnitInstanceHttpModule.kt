package org.katan.service.unit.instance.http

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import org.katan.http.HttpModule
import org.katan.http.HttpModuleRegistry
import org.katan.service.unit.instance.http.routes.kill
import org.katan.service.unit.instance.http.routes.restart
import org.katan.service.unit.instance.http.routes.start
import org.katan.service.unit.instance.http.routes.stop

internal class UnitInstanceHttpModule(registry: HttpModuleRegistry) : HttpModule(registry) {

    override fun install(app: Application) {
        app.routing {
            start()
            stop()
            kill()
            restart()
        }
    }
}
