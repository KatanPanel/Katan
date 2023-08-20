package org.katan.service.instance

import kotlinx.coroutines.flow.Flow
import org.katan.model.Snowflake
import org.katan.model.instance.InstanceInternalStats
import org.katan.model.instance.InstanceNotFoundException
import org.katan.model.instance.InstanceUpdateCode
import org.katan.model.instance.UnitInstance
import org.katan.service.instance.model.CreateInstanceOptions

interface InstanceService {

    /**
     * Returns an [UnitInstance] with the given [id].
     *
     * @throws InstanceNotFoundException
     */
    suspend fun getInstance(id: Snowflake): UnitInstance

    suspend fun deleteInstance(instance: UnitInstance)

    suspend fun createInstance(blueprintId: Snowflake, options: CreateInstanceOptions): UnitInstance

    suspend fun updateInstanceStatus(instance: UnitInstance, code: InstanceUpdateCode)

    suspend fun getInstanceLogs(id: Snowflake): Flow<String>

    suspend fun runInstanceCommand(id: Snowflake, command: String)

    suspend fun streamInternalStats(id: Snowflake): Flow<InstanceInternalStats>
}
