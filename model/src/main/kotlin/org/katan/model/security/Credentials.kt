package org.katan.model.security

public interface Credentials

public data class TokenCredentials(val token: String) : Credentials
