package me.devnatan.katan.database

interface DatabaseSettings {

    val url: String?

    val dialect: String?

    val host: String

    val user: String

    val password: String

    val database: String

    val properties: Map<String, String>

}

private data class SettingsImpl(
    override val url: String?,
    override val dialect: String?,
    override val host: String,
    override val user: String,
    override val password: String,
    override val database: String,
    override val properties: Map<String, String>
) : DatabaseSettings

fun DatabaseSettings(
    url: String? = null,
    dialect: String? = null,
    host: String = "",
    user: String = "",
    password: String = "",
    database: String = "",
    properties: Map<String, String> = emptyMap()
): DatabaseSettings = SettingsImpl(url, dialect, host, user, password, database, properties)