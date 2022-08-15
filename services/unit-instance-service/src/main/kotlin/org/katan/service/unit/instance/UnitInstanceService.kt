package org.katan.service.unit.instance

import org.katan.model.instance.UnitInstance
import org.katan.model.instance.UnitInstanceUpdateStatusCode
import kotlin.jvm.Throws

public interface UnitInstanceService {

    @Throws(InstanceException::class)
    public suspend fun getInstance(id: Long): UnitInstance

    public suspend fun deleteInstance(instance: UnitInstance)

    public suspend fun createInstanceFor(image: String): UnitInstance

    public suspend fun updateInstanceStatus(
        instance: UnitInstance,
        code: UnitInstanceUpdateStatusCode
    )
}
