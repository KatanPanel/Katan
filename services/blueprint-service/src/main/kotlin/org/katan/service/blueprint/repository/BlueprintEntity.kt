package org.katan.service.blueprint.repository

import kotlinx.datetime.Instant

interface BlueprintEntity {

    var name: String

    var image: String

    var createdAt: Instant

    fun getId(): Long

}