package org.katan.service.projects.http

import io.ktor.server.application.Application
import io.ktor.server.auth.authenticate
import io.ktor.server.routing.routing
import org.katan.http.HttpModule

internal class ProjectsHttpModule : HttpModule() {

    override fun install(app: Application) {
        app.routing {
            authenticate {

            }
        }
    }
}