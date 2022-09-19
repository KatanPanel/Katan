package org.katan.service.blueprint.http

import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import org.katan.http.di.HttpModule
import org.katan.http.di.HttpModuleRegistry
import org.katan.service.blueprint.http.routes.getBlueprint
import org.katan.service.blueprint.http.routes.importBlueprints
import org.katan.service.blueprint.http.routes.listBlueprints
import org.katan.service.blueprint.http.routes.readBlueprintFile

internal class BlueprintHttpModule(registry: HttpModuleRegistry) : HttpModule(registry) {

    override fun install(app: Application) {
        app.apply {
            routing {
                getBlueprint()
                listBlueprints()
                importBlueprints()
                readBlueprintFile()
            }
        }
    }
}
