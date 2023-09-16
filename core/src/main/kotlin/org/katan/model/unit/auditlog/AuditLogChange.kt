package org.katan.model.unit.auditlog

import kotlinx.serialization.Serializable

@Serializable
data class AuditLogChange(
    val key: String,
    val oldValue: String?,
    val newValue: String?,
)
