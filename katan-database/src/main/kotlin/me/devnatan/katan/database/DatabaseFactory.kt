package me.devnatan.katan.database

class DatabaseFactory {

    private val connectors: MutableSet<DatabaseConnector> = mutableSetOf()
    private val mutex = Any()

    fun register(connector: DatabaseConnector) = synchronized(mutex) {
        connectors.add(connector)
    }

    fun fromUrl(url: String): DatabaseConnector {
        return connectors.firstOrNull {
            it.isUrlSupported(url)
        } ?: throw IllegalArgumentException("No connectors available for database url: $url")
    }

    fun fromDialect(dialect: String): DatabaseConnector {
        return connectors.firstOrNull {
            it.isDialectSupported(dialect)
        } ?: throw IllegalArgumentException("No connectors available for database dialect: $dialect")
    }


}