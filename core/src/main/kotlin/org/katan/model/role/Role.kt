package org.katan.model.role

import kotlinx.serialization.Serializable
import org.katan.model.Permissions
import org.katan.model.PermissionsHolder
import org.katan.model.Snowflake

/**
 * Roles are a set of permissions attached to a group of accounts.
 */
@Serializable
data class Role(
    val id: Snowflake,
    val name: String,
    val position: Int,
    override val permissions: Permissions,
) : PermissionsHolder
