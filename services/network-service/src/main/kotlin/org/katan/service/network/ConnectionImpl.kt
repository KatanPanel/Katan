package org.katan.service.network

import org.katan.model.Connection

@kotlinx.serialization.Serializable
internal data class ConnectionImpl(override val host: String, override val port: Int) : Connection