package me.devnatan.katan.api.security

import me.devnatan.katan.api.security.account.Account

/**
 * Represents security credentials.
 *
 * Credentials are used for many different forms of protection or identification.
 * A basic type of credential is in [Account] passwords, which uses [PasswordCredential].
 */
interface Credential

object EmptyCredential : Credential

inline class PasswordCredential(val password: String) : Credential