package org.katan.service.server

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.katan.service.container.ContainerCreateOptions
import org.katan.service.container.ContainerFactory
import java.util.UUID

public class InMemoryServerService(
    private val containerFactory: ContainerFactory
) : ServerService {

    public companion object {
        public const val IMAGE_NAME: String = "mock"
    }

    private val registered: List<Server> = mutableListOf()
    private val mutex = Mutex()

    override suspend fun get(id: String): Server? = mutex.withLock {
        registered.firstOrNull {
            it.id == id
        }
    }

    override suspend fun create(options: ServerCreateOptions): Server = mutex.withLock {
        ServerImpl(
            id = UUID.randomUUID().toString(),
            name = options.name,
            container = containerFactory.create(ContainerCreateOptions(IMAGE_NAME))
        )
    }

}