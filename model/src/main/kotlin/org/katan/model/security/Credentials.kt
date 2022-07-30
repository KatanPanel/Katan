package org.katan.model.security;

interface Credentials

data class TokenCredentials(val token: String) : Credentials