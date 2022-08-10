package org.katan.model.unit.auditlog

/**
 * All audit log events type ids.
 *
 * @see AuditLogEntry
 */
object AuditLogEvents {

    const val UnitCreate = 1u
    const val UnitUpdate = 2u
    const val UnitDelete = 3u
    const val RoleCreate = 4u
    const val RoleUpdate = 5u
    const val RoleDelete = 6u
}
