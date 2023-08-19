package org.katan.model.unit.auditlog

/**
 * All audit log events type ids.
 *
 * @see AuditLogEntry
 */
public object AuditLogEvents {

    public const val UnitCreate: UInt = 1u
    public const val UnitUpdate: UInt = 2u
    public const val UnitDelete: UInt = 3u
    public const val RoleCreate: UInt = 4u
    public const val RoleUpdate: UInt = 5u
    public const val RoleDelete: UInt = 6u
}
