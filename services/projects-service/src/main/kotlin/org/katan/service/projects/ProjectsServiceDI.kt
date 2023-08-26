package org.katan.service.projects

import org.koin.core.module.Module
import org.koin.dsl.module

public val projectServiceDI: Module = module {
    single<ProjectsService> {
        ProjectsServiceImpl()
    }
}