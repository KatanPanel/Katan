package org.katan.service.projects

import org.katan.EventsDispatcher
import org.katan.model.project.Project
import org.katan.service.projects.model.CreateProjectOptions

internal class ProjectsServiceImpl(
    private val eventsDispatcher: EventsDispatcher,
) : ProjectsService {
    override suspend fun getProject(name: String): Project {
        TODO("Not yet implemented")
    }

    override suspend fun createProject(options: CreateProjectOptions): Project {
        TODO("Not yet implemented")
    }
}