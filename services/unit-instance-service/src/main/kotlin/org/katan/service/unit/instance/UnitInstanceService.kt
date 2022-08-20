package org.katan.service.unit.instance

import kotlinx.coroutines.flow.Flow
import org.katan.model.instance.InstanceUpdateCode
import org.katan.model.instance.UnitInstance

public interface UnitInstanceService {

    public suspend fun getInstance(id: Long): UnitInstance

    public suspend fun deleteInstance(instance: UnitInstance)

    public suspend fun createInstance(image: String, host: String?, port: Int?): UnitInstance

    public suspend fun updateInternalStatus(
        instance: UnitInstance,
        code: InstanceUpdateCode
    )

    public suspend fun fetchInstanceLogs(id: Long): Flow<String>

    public suspend fun executeInstanceCommand(id: Long, command: String)

}
