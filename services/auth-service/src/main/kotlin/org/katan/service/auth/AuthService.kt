package org.katan.service.auth

import org.katan.model.account.Account

public interface AuthService {

    public suspend fun auth(username: String, password: String): String

    public suspend fun verify(subject: String?): Account?
}
