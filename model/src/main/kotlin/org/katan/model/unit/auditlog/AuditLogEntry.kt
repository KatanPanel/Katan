package org.katan.model.unit.auditlog

/**
 * An audit log entry represents a single event action indicated by a action type that can container
 * one to many changes that affect an entity.
 *
 * The structure of an entry's changes will be different depending on its type.
 */
interface AuditLogEntry {

    /**
     * The unique ID of this audit log entry.
     */
    val id: Long

    /**
     * ID of the affected entity.
     */
    val targetId: Long

    /**
     * ID of the account or external application that made the changes.
     */
    val actorId: Long?

    /**
     * The type of event occurred.
     */
    val event: AuditLogEvent

    /**
     * Reason for the changes.
     */
    val reason: String?

    /**
     * Changes made to the affected entity.
     */
    val changes: List<AuditLogChange>

    /**
     * Additional data for certain event types.
     */
    val additionalData: AuditLogAdditionalData
}
