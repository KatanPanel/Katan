package me.devnatan.katan.core.database

import com.typesafe.config.Config
import me.devnatan.katan.common.util.get
import me.devnatan.katan.common.util.getStringMap
import me.devnatan.katan.core.database.jdbc.H2Connector
import me.devnatan.katan.core.database.jdbc.JDBCLocalSettings
import me.devnatan.katan.core.database.jdbc.JDBCRemoteSettings
import me.devnatan.katan.core.database.jdbc.MySQLConnector

val SUPPORTED_CONNECTORS: Map<String, (Config) -> Pair<DatabaseConnector, DatabaseSettings>> = mapOf(
    "mysql" to {
        MySQLConnector() to JDBCRemoteSettings(
            it.get("host", "localhost:3306"),
            it.get("user", "root"),
            it.getString("password"),
            it.get("database", "katan"),
            it.getStringMap("properties")
        )
    },
    "h2" to {
        H2Connector(it.get("inMemory", true)) to JDBCLocalSettings(
            it.get("file", "./katan.db"),
            it.getStringMap("properties")
        )
    }
)