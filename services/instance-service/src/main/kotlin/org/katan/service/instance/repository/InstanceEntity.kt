package org.katan.service.instance.repository

import kotlinx.datetime.Instant

interface InstanceEntity {

    fun getId(): Long

    var updatePolicy: String

    var containerId: String?

    var blueprintId: Long

    var host: String?

    var port: Short?

    var status: String

    var createdAt: Instant
}
