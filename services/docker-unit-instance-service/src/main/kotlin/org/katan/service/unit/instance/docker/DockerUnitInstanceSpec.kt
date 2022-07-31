package org.katan.service.unit.instance.docker

import org.katan.service.unit.instance.UnitInstanceSpec

internal const val KIND = "docker"
internal const val IMAGE_PROPERTY = "image"

internal class DockerUnitInstanceSpec(
    val image: String
) : UnitInstanceSpec {

    override val kind: String get() = KIND

    override val data: Map<String, Any> by lazy {
        mapOf(IMAGE_PROPERTY to image)
    }
}
