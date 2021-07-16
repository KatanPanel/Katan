package me.devnatan.katan.core.database.dto

import java.util.*

data class ServerHolderDTO(
    val id: UUID,
    val isOwner: Boolean
)