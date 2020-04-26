package me.devnatan.katan.api.account

import java.util.*

interface KAccount {

    /**
     * Account ID
     */
    val id: UUID

    /**
     * Account username.
     */
    val username: String

    /**
     * Account password.
     */
    var password: String

}