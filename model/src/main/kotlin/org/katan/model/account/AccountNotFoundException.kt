package org.katan.model.account

class AccountNotFoundException(val accountUsername: String) : RuntimeException()

class InvalidAccountCredentialsException : RuntimeException()