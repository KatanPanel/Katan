package org.katan.service.unit.repository

import kotlinx.datetime.Instant

public interface UnitEntity {

    public var nodeId: Int

    public var externalId: String?

    public var name: String

    public var createdAt: Instant

    public var updatedAt: Instant

    public var deletedAt: Instant?

    public var instanceId: Long?

    public var status: String

    public fun getId(): Long
}
