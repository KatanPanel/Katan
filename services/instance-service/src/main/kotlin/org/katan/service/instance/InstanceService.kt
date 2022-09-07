package org.katan.service.instance

import kotlinx.coroutines.flow.Flow
import org.katan.model.blueprint.RawBlueprint
import org.katan.model.instance.InstanceInternalStats
import org.katan.model.instance.InstanceUpdateCode
import org.katan.model.instance.UnitInstance

interface InstanceService {

    suspend fun getInstance(id: Long): UnitInstance

    suspend fun deleteInstance(instance: UnitInstance)

    suspend fun createInstance(
        image: String,
        blueprint: RawBlueprint,
        host: String?,
        port: Int?
    ): UnitInstance

    suspend fun updateInstanceStatus(
        instance: UnitInstance,
        code: InstanceUpdateCode
    )

    suspend fun getInstanceLogs(id: Long): Flow<String>

    suspend fun runInstanceCommand(id: Long, command: String)

    suspend fun streamInternalStats(id: Long): Flow<InstanceInternalStats>

}
