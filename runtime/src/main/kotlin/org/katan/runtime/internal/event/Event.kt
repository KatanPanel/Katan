package org.katan.runtime.internal.event

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Event(val type: String, val actor: String, val properties: Map<String, @Contextual Any>)