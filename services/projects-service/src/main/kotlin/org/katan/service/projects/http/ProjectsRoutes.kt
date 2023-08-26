package org.katan.service.projects.http

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/blueprints")
internal class ProjectsRoutes {

    @Serializable
    @Resource("")
    internal class List(val parent: ProjectsRoutes = ProjectsRoutes())
}
