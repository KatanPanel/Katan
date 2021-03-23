package me.devnatan.katan.api.security.role

import me.devnatan.katan.api.Descriptor
import me.devnatan.katan.api.security.permission.PermissionsHolder
import java.time.Instant

/**
 * Roles are entities that carry permissions with a many-to-one relationship,
 * with `one` being the role itself, and `many` being the entity that has a
 * [Role] as a property.
 *
 * The [Role]-entity relationship is an inheritance relationship, as soon as
 * the role has a permission granted and the entity does not have that
 * permission  defined, the entity will inherit the role's permission.
 *
 * Roles are simple objects that contain a [Descriptor] to identify them.
 *
 * @author Natan Vieira
 * @since  1.0
 */
interface Role : PermissionsHolder {

    /**
     * Returns the role descriptor, the id contained in this [descriptor]
     * is actually an [Int] and must be treated properly.
     */
    val descriptor: Descriptor

    /**
     * Returns the role name.
     */
    var name: String

    /**
     * Returns the [Instant] this role was created.
     */
    val createdAt: Instant

}