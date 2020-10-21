package me.devnatan.katan.api.security.role

import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.security.permission.PermissionsHolder
import java.time.Instant

@UnstableKatanApi
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