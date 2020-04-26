package me.devnatan.katan.api.account

import java.util.*

data class KUserAccount(
    override val id: UUID,
    override val username: String,
    override var password: String
) : KAccount