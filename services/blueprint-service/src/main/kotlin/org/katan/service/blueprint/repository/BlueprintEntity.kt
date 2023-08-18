package org.katan.service.blueprint.repository

import kotlinx.datetime.Instant
import org.katan.model.Snowflake

interface BlueprintEntity {

    var name: String

    var version: String

    var imageId: String

    var createdAt: Instant

    var updatedAt: Instant?

    fun getId(): Snowflake
}
