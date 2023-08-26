package org.katan.service.projects

import org.katan.model.project.Project

internal class ProjectsServiceImpl : ProjectsService {
    override suspend fun getProject(name: String): Project {
        TODO("Not yet implemented")
    }
}