package me.devnatan.katan.database.dto.account

import java.time.Instant
import java.util.*

class AccountDTO(
    val id: UUID,
    val username: String,
    val registeredAt: Instant,
    val lastLogin: Instant?,
    val password: String?
)