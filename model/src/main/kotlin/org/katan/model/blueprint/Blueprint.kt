package org.katan.model.blueprint

import kotlinx.datetime.Instant
import org.katan.model.Snowflake

interface Blueprint {

    val id: Snowflake

    val name: String

    val version: String

    val imageId: String

    val createdAt: Instant

    val updatedAt: Instant?
}
