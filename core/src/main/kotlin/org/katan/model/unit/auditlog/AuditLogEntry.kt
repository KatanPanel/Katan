package org.katan.model.unit.auditlog

import kotlinx.datetime.Instant
import org.katan.model.Snowflake

/**
 * An audit log entry represents a single event action indicated by a action type that can container
 * one to many changes that affect an entity.
 *
 * The structure of an entry's changes will be different depending on its type.
 */
public interface AuditLogEntry {

    /**
     * The unique ID of this audit log entry.
     */
    public val id: Snowflake

    /**
     * ID of the affected entity.
     */
    public val targetId: Snowflake

    /**
     * ID of the account or external application that made the changes.
     */
    public val actorId: Snowflake?

    /**
     * The type of event occurred.
     */
    public val event: AuditLogEvent

    /**
     * Reason for the changes.
     */
    public val reason: String?

    /**
     * Changes made to the affected entity.
     */
    public val changes: List<AuditLogChange>

    /**
     * Additional data for certain event types.
     */
    public val additionalData: String?

    public val createdAt: Instant
}
