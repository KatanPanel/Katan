package org.katan.service.blueprint.repository

import kotlinx.datetime.Instant
import org.katan.model.Snowflake

internal interface BlueprintRepository {

    suspend fun findAll(): List<BlueprintEntity>

    suspend fun find(id: Long): BlueprintEntity?

    suspend fun create(id: Long, spec: ByteArray, createdAt: Instant)

    suspend fun delete(id: Long)
}
