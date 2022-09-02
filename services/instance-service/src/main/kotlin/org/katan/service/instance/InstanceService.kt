package org.katan.service.instance

import kotlinx.coroutines.flow.Flow
import org.katan.model.instance.InstanceUpdateCode
import org.katan.model.instance.UnitInstance

interface InstanceService {

    suspend fun getInstance(id: Long): UnitInstance

    suspend fun deleteInstance(instance: UnitInstance)

    suspend fun createInstance(image: String, host: String?, port: Int?): UnitInstance

    suspend fun updateInternalStatus(
        instance: UnitInstance,
        code: InstanceUpdateCode
    )

    suspend fun fetchInstanceLogs(id: Long): Flow<String>

    suspend fun executeInstanceCommand(id: Long, command: String)
}
