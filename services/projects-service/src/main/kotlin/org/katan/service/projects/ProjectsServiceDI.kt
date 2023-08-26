package org.katan.service.projects

import org.katan.service.projects.repository.ProjectsRepository
import org.katan.service.projects.repository.RemoteProjectsRepository
import org.koin.core.module.Module
import org.koin.dsl.module

public val projectServiceDI: Module = module {
    single<ProjectsService> {
        ProjectsServiceImpl(eventsDispatcher = get())
    }
    single<ProjectsRepository> {
        RemoteProjectsRepository(database = get())
    }
}