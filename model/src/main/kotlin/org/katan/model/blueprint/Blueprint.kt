package org.katan.model.blueprint

import kotlinx.datetime.Instant

interface Blueprint {

    val id: Long

    val name: String

    val version: String

    val imageId: String

    val createdAt: Instant

    val updatedAt: Instant
}
