package org.katan.model.role

import org.katan.model.PermissionsHolder
import org.katan.model.Snowflake

/**
 * Roles are a set of permissions attached to a group of accounts.
 */
public interface Role : PermissionsHolder {

    /**
     * The unique id of this role in.
     */
    public val id: Snowflake

    /**
     * The unique name of this role.
     */
    public val name: String

    /**
     * The position of this role.
     */
    public val position: Int
}
