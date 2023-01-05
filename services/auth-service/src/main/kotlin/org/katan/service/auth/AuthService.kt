package org.katan.service.auth

import org.katan.model.account.Account

interface AuthService {

    suspend fun auth(username: String, password: String): String

    suspend fun verify(subject: String?): Account?
}
