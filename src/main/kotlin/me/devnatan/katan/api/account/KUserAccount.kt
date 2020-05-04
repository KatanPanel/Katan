package me.devnatan.katan.api.account

import java.util.*

/**
 * @property password account password.
 */
data class KUserAccount(
    override val id: UUID,
    override val username: String,
    var password: String
) : KAccount {

    override var permissions: Int = 0

}