package org.katan.service.blueprint.repository

import org.katan.model.blueprint.Blueprint

interface BlueprintRepository {

    suspend fun findAll(): List<BlueprintEntity>

    suspend fun find(id: Long): BlueprintEntity?

    suspend fun create(blueprint: Blueprint)

    suspend fun delete(id: Long)

}