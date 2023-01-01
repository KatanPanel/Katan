package org.katan.service.db

import org.jetbrains.exposed.sql.Database

interface DatabaseService {

    fun get(): Database
}
