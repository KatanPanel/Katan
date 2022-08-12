package org.katan.model.instance

import org.katan.model.unit.ImageUpdatePolicy

interface UnitInstance {
    val id: Long

    val status: UnitInstanceStatus

    val containerId: String

    val imageId: String

    val imageUpdatePolicy: ImageUpdatePolicy
}