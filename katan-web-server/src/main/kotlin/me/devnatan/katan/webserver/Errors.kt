package me.devnatan.katan.webserver

val INVALID_ACCESS_TOKEN_ERROR = 1000 to "Invalid access token"
val INVALID_SESSION_ERROR = 1001 to "Invalid session"
val ACCOUNT_MISSING_CREDENTIALS_ERROR = 2001 to "Missing account credentials"
val ACCOUNT_INVALID_CREDENTIALS_ERROR = 2002 to "Invalid account username or wrong password"
val ACCOUNT_ALREADY_EXISTS_ERROR = 2003 to "Account already exists"
val ACCOUNT_NOT_FOUND_ERROR = 2004 to "Account not found"