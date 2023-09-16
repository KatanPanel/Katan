package org.katan.model.net

import kotlinx.serialization.Serializable

@Serializable
data class HostPort(val host: String, val port: Short)
