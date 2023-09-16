package org.katan.model

/**
 * Permission are a way to limit certain abilities to any entity that are a [PermissionsHolder]
 * in Katan. A set of base permissions can be configured at [Unit] level for different roles.
 * When these roles are attached to permissions holder, they grant or revoke specific privileges
 * within the unit. Along with the unit-level permissions, permission overwrites are also supported,
 * that can be assigned to individual permissions holders.
 *
 * Permissions are stored in a variable-length [Int] serialized into a [String] and calculated
 * using Bitwise Operations, the total permissions value can be determined by [Int.or]-ing together
 * each individual value and flags can be checked using [Int.and] operations.
 */
typealias Permissions = UInt

/**
 * A permission-holding entity whose specific permissions are required to perform certain actions in
 * its lifecycle.
 */
interface PermissionsHolder {

    /**
     * Permissions bit set of this entity.
     */
    val permissions: Permissions
}
