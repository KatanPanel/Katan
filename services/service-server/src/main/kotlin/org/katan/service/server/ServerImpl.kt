package org.katan.service.server

import org.katan.service.container.Container

@kotlinx.serialization.Serializable
public data class ServerImpl(
    override val id: String,
    override val name: String,
    override val container: Container
) : Server