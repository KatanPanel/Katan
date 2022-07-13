package org.katan.service.server

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock
import org.katan.config.KatanConfig
import org.katan.model.unit.KUnit
import org.katan.service.id.IdService

public class LocalUnitServiceImpl(
    private val idService: IdService,
    private val config: KatanConfig
) : UnitService {

    private val registered: MutableList<KUnit> = mutableListOf()
    private val mutex = Mutex()

    override suspend fun getUnit(id: Long): KUnit? = mutex.withLock {
        registered.firstOrNull {
            it.id == id
        }
    }

    override suspend fun createUnit(options: UnitCreateOptions): KUnit {
        mutex.withLock {
            if (registered.any { it.name.equals(options.name, ignoreCase = true) })
                throw UnitConflictException()
        }

        val unit = createUnit0(options)

        mutex.withLock {
            registered.add(unit)
        }

        return unit
    }

    private suspend fun createUnit0(options: UnitCreateOptions): KUnit {
        val currentInstant = Clock.System.now()
        val unitId = idService.generate()

        return UnitImpl(
            id = unitId,
            externalId = options.externalId,
            nodeId = config.nodeId,
            name = options.name,
            displayName = options.displayName,
            description = options.description,
            createdAt = currentInstant,
            updatedAt = currentInstant
        )
    }

}