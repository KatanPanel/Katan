package org.katan.service.auth

public interface AuthService {

    public suspend fun auth(username: String, password: String): String
}
