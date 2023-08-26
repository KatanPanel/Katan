package org.katan.service.projects.repository

import kotlinx.datetime.Instant
import org.katan.model.Snowflake

internal interface ProjectsRepository {

    /**
     * Returns a list containing registered projects.
     */
    suspend fun list(): List<ProjectEntity>

    /**
     * Finds a project by its identifier. Returns `null` if there's no project with the given id.
     *
     *  @param id The project id.
     */
    suspend fun findById(id: Snowflake): ProjectEntity?

    /**
     * Creates a new project with the given parameters.
     *
     * @param id The project id.
     * @param name The project name.
     * @param createdAt The momment that the project has been created.
     */
    suspend fun create(id: Snowflake, name: String, createdAt: Instant)
}