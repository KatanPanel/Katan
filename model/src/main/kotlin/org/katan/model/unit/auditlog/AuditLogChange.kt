package org.katan.model.unit.auditlog

/**
 * Changes made to an entity in an audit log entry.
 *
 * @see AuditLogEntry
 */
public interface AuditLogChange {

    /**
     * Name of the affected entity by this change.
     */
    public val key: String

    /**
     * The old value of the key.
     */
    public val oldValue: String?

    /**
     * The new value of the key.
     */
    public val newValue: String?
}
