package me.devnatan.katan.api.security.permission

import java.time.Instant

/**
 * @author Natan Vieira
 * @since  1.0
 */
interface Permission {

    val key: PermissionKey

    val value: PermissionFlag

    val givenAt: Instant

    var lastModifiedAt: Instant

}