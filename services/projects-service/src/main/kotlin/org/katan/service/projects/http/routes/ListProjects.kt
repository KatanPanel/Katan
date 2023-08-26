package org.katan.service.projects.http.routes

import io.ktor.server.routing.Route
import org.katan.service.projects.ProjectsService
import org.katan.service.projects.http.ProjectsRoutes
import org.koin.ktor.ext.inject
import io.ktor.server.resources.get

internal fun Route.listProjects() {
    val projectsService by inject<ProjectsService>()

    get<ProjectsRoutes.List> {
        val projects = projectsService
    }
}