package org.katan.service.server

import kotlinx.datetime.Clock
import org.katan.model.Connection
import org.katan.model.unit.KUnit
import org.katan.service.id.IdService

internal class DefaultUnitFactory(
    private val idService: IdService
) : UnitFactory {

    override suspend fun create(options: UnitCreateOptions): KUnit {
        val currentInstant = Clock.System.now()
        val address = object : Connection {
            override val host: String
                get() = "0.0.0.0"

            override val port: Short
                get() = 25565
        }

        return UnitImpl(
            id = idService.generate(),
            externalId = options.externalId,
            nodeId = 0, // TODO correct node id
            name = options.name,
            displayName = options.displayName,
            description = options.description,
            createdAt = currentInstant,
            updatedAt = currentInstant,
            remoteAddress = address,
            localAddress = address
        )
    }

}