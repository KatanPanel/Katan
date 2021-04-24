package me.devnatan.katan.database

import me.devnatan.katan.database.repository.AccountsRepository
import me.devnatan.katan.database.repository.ServersRepository

class DatabaseRepositories(
    val accounts: AccountsRepository,
    val servers: ServersRepository
)