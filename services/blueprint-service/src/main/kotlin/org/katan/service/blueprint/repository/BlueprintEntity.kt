package org.katan.service.blueprint.repository

import kotlinx.datetime.Instant

interface BlueprintEntity {

    var name: String

    var version: String

    var imageId: String

    var createdAt: Instant

    var updatedAt: Instant?

    fun getId(): Long

}