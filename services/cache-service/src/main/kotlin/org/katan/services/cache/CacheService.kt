package org.katan.services.cache

public interface CacheService {

    public suspend fun get(key: String): String

    public suspend fun set(key: String, value: String): String
}
