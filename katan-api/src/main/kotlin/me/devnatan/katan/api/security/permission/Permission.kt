package me.devnatan.katan.api.security.permission

import java.time.Instant

interface Permission {

    val key: PermissionKey

    val value: PermissionFlag

    val givenAt: Instant

    var lastModified: Instant

}