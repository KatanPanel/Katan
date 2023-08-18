package org.katan.service.instance.repository

import org.katan.model.Snowflake

interface InstanceEntity {

    var updatePolicy: String

    var containerId: String?

    var blueprintId: Snowflake

    var host: String?

    var port: Short?

    var status: String

    fun getId(): Snowflake
}
