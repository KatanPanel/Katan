package org.katan.service.unit.instance.repository

import org.katan.model.instance.UnitInstance

public interface UnitInstanceRepository {

    public suspend fun findById(id: Long): InstanceEntity?

    public suspend fun create(instance: UnitInstance)

    public suspend fun delete(id: Long)

    public suspend fun update(id: Long, update: InstanceEntity.() -> Unit): InstanceEntity?
}
