package org.katan.service.instance

import kotlinx.coroutines.flow.Flow
import org.katan.model.Snowflake
import org.katan.model.instance.InstanceInternalStats
import org.katan.model.instance.InstanceUpdateCode
import org.katan.model.instance.UnitInstance
import org.katan.service.instance.model.CreateInstanceOptions

interface InstanceService {

    suspend fun getInstance(id: Long): UnitInstance

    suspend fun deleteInstance(instance: UnitInstance)

    suspend fun createInstance(blueprintId: Snowflake, options: CreateInstanceOptions): UnitInstance

    suspend fun updateInstanceStatus(instance: UnitInstance, code: InstanceUpdateCode)

    suspend fun getInstanceLogs(id: Long): Flow<String>

    suspend fun runInstanceCommand(id: Long, command: String)

    suspend fun streamInternalStats(id: Long): Flow<InstanceInternalStats>
}
