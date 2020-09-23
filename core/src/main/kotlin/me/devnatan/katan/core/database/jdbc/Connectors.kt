package me.devnatan.katan.core.database.jdbc

class MySQLConnector : JDBCRemoteConnector("MySQL", "com.mysql.cj.jdbc.Driver", "jdbc:mysql://{host}/{database}")

class H2Connector(private val memory: Boolean) : JDBCLocalConnector("H2", "org.h2.Driver",
    if (memory) "jdbc:h2:{file}"
    else "jdbc:h2:mem:regular;{properties}"
) {

    override fun createConnectionUrl(settings: JDBCLocalSettings): String {
        return if (!memory) super.createConnectionUrl(settings)
        else url.replace("{properties}", settings.connectionProperties.entries.joinToString(";") {
            it.key + "=" + it.value
        })
    }

}