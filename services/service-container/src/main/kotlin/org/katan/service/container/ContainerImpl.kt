package org.katan.service.container

@kotlinx.serialization.Serializable
internal data class ContainerImpl(
    override val id: String,
    override val runtimeIdentifier: String
) : Container