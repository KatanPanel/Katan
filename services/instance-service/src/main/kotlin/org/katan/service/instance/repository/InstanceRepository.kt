package org.katan.service.instance.repository

import org.katan.model.instance.UnitInstance

interface InstanceRepository {

    suspend fun findById(id: Long): InstanceEntity?

    suspend fun create(instance: UnitInstance)

    suspend fun delete(id: Long)

    suspend fun update(id: Long, update: InstanceEntity.() -> Unit): InstanceEntity?
}
