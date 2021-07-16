package me.devnatan.katan.database

interface DatabaseSettings {

    operator fun <T> get(key: String): T? {
        throw UnsupportedOperationException("Not implemented")
    }

}