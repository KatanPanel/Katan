package org.katan.service.container

import java.math.BigInteger
import java.util.UUID
import kotlin.random.Random
import kotlin.random.asJavaRandom

public class FakeContainerFactory : ContainerFactory {

    override suspend fun generateId(): String {
        return UUID.randomUUID().toString()
    }

    private fun randomContainerId(): String {
        return BigInteger(130, Random.asJavaRandom()).toString(32)
    }

    override suspend fun create(options: ContainerCreateOptions): Container {
        return ContainerImpl(
            id = generateId(),
            runtimeIdentifier = randomContainerId()
        )
    }

}