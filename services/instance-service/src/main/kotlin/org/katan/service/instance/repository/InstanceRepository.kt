package org.katan.service.instance.repository

import org.katan.model.Snowflake
import org.katan.model.instance.UnitInstance

interface InstanceRepository {

    suspend fun findById(id: Snowflake): InstanceEntity?

    suspend fun create(instance: UnitInstance)

    suspend fun delete(id: Snowflake)

    suspend fun update(id: Snowflake, update: InstanceEntity.() -> Unit): InstanceEntity?
}
