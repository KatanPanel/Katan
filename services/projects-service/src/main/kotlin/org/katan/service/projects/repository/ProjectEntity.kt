package org.katan.service.projects.repository

import kotlinx.datetime.Instant

internal interface ProjectEntity {

    val entityId: Long

    var name: String

    var createdAt: Instant
}