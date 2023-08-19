package org.katan.service.blueprint.repository

import kotlinx.datetime.Instant
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.katan.model.Snowflake

internal interface BlueprintEntity {

    fun getId(): Snowflake

    var createdAt: Instant

    var updatedAt: Instant

    var content: ExposedBlob
}
