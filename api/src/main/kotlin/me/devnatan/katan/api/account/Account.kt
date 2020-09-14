package me.devnatan.katan.api.account

import me.devnatan.katan.api.permission.PermissionHolder
import java.util.*

interface Account : PermissionHolder {

    /**
     * Account ID
     */
    val id: UUID

    /**
     * Account username.
     */
    val username: String

    /**
     * Account password
     */
    var password: String

}