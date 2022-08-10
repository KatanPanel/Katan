package org.katan.model.unit

interface UnitInstance {
    val id: Long

    val status: UnitInstanceStatus

    val containerId: String

    val imageId: String

    val imageUpdatePolicy: ImageUpdatePolicy
}
