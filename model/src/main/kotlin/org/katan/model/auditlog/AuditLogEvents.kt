package org.katan.model.auditlog

/**
 * All audit log events type ids.
 *
 * @see AuditLogEntry
 */
object AuditLogEvents {

    const val UnitCrate = 1
    const val UnitUpdate = 2
    const val UnitDelete = 3
    const val RoleCreate = 4
    const val RoleUpdate = 5
    const val RoleDelete = 6

}