package org.katan.service.projects

import org.katan.model.project.Project
import org.katan.model.project.ProjectNotFoundException
import org.katan.service.projects.model.CreateProjectOptions

public interface ProjectsService {

    /**
     * Returns a [Project] with the specified [id].
     *
     * @param id The project id.
     * @throws ProjectNotFoundException If there's no project with the given name.
     */
    public suspend fun getProject(id: String): Project

    public suspend fun createProject(options: CreateProjectOptions): Project
}