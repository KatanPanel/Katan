package org.katan.service.db

import org.jetbrains.exposed.sql.Database

public interface DatabaseService {

    public fun get(): Database
}
