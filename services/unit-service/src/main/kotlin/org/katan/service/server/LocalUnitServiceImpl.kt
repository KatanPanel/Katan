package org.katan.service.server

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.katan.model.unit.KUnit

public class LocalUnitServiceImpl(
    private val unitFactory: UnitFactory
) : UnitService {

    public companion object {
        public const val IMAGE_NAME: String = "mock"
    }

    private val registered: MutableList<KUnit> = mutableListOf()

    private val mutex = Mutex()

    override suspend fun list(): List<KUnit> {
        return registered.toList()
    }

    override suspend fun get(id: Long): KUnit? = mutex.withLock {
        registered.firstOrNull {
            it.id == id
        }
    }

    override suspend fun create(options: UnitCreateOptions): KUnit {
        val unit = unitFactory.create(options)

        mutex.withLock {
            if (!registered.add(unit))
                throw UnitConflictException()
        }

        return unit
    }

}