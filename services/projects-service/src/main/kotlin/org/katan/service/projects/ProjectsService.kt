package org.katan.service.projects

import org.katan.model.project.Project
import org.katan.model.project.ProjectNotFoundException

public interface ProjectsService {

    /**
     * Returns a [Project] with the specified [name].
     *
     * @param name The project name.
     * @throws ProjectNotFoundException If there's no project with the given name.
     */
    public suspend fun getProject(name: String): Project
}