package org.katan.model.unit.auditlog

typealias AuditLogEvent = UInt

object AuditLogEvents {

    const val UnitCreate: UInt = 1u
    const val UnitUpdate: UInt = 2u
    const val UnitDelete: UInt = 3u
    const val RoleCreate: UInt = 4u
    const val RoleUpdate: UInt = 5u
    const val RoleDelete: UInt = 6u
}
