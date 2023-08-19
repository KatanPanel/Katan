package org.katan.service.instance.repository

import kotlinx.datetime.Instant
import org.katan.model.Snowflake

interface InstanceEntity {

    fun getId(): Snowflake

    var updatePolicy: String

    var containerId: String?

    var blueprintId: Snowflake

    var host: String?

    var port: Short?

    var status: String

    var createdAt: Instant
}
