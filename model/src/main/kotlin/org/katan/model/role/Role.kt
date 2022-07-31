package org.katan.model.role

import org.katan.model.permission.PermissionsHolder

/**
 * Roles are a set of permissions attached to a group of accounts.
 */
interface Role : PermissionsHolder {

    /**
     * The unique id of this role in.
     */
    val id: Long

    /**
     * The unique name of this role.
     */
    val name: String

    /**
     * The position of this role.
     */
    val position: Int
}
