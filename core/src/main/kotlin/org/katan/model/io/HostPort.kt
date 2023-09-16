package org.katan.model.io

import kotlinx.serialization.Serializable

@Serializable
public data class HostPort(public val host: String, public val port: Short)
