package org.katan.service.container

internal data class ContainerImpl(
    override val id: String,
    override val runtimeIdentifier: String
) : Container