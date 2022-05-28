package org.katan.service.server

import kotlinx.serialization.Serializable
import org.katan.service.container.Container

@Serializable
public data class ServerImpl(
    override val id: String,
    override val name: String,
    override val container: Container
) : Server