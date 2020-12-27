package me.devnatan.katan.api.role

import me.devnatan.katan.api.security.permission.PermissionsHolder
import java.time.Instant

interface Role : PermissionsHolder {

    /**
     * Returns the role identification number.
     */
    val id: Int

    /**
     * Returns the role name.
     */
    val name: String

    /**
     * Returns when the role was created.
     */
    val createdAt: Instant

}