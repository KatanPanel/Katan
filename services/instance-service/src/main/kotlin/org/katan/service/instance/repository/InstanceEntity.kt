package org.katan.service.instance.repository

interface InstanceEntity {

    var updatePolicy: String

    var containerId: String?

    var host: String?

    var port: Short?

    var status: String

    fun getId(): Long
}
