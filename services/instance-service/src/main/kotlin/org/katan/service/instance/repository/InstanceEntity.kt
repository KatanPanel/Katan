package org.katan.service.instance.repository

public interface InstanceEntity {

    public var updatePolicy: String

    public var containerId: String?

    public var host: String?

    public var port: Short?

    public var status: String

    public fun getId(): Long
}
