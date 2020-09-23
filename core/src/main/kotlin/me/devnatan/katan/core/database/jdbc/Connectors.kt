package me.devnatan.katan.core.database.jdbc

import me.devnatan.katan.core.database.DatabaseSettings

class MySQLConnector : JDBCConnector("MySQL", "com.mysql.cj.jdbc.Driver", "jdbc:mysql://{host}/{database}")

class H2Connector(private val memory: Boolean) : JDBCConnector("H2", "org.h2.Driver",
    if (memory) "jdbc:h2:{file}"
    else "jdbc:h2:mem:regular;{properties}"
) {

    override fun createConnectionUrl(settings: DatabaseSettings): String {
        require(settings is JDBCLocalSettings)
        var url = super.createConnectionUrl(settings)
        if (memory)
            url = url.replace("{properties}", settings.connectionProperties.entries.joinToString(";") {
                it.key + "=" + it.value
            })

        return url
    }

}