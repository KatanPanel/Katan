package org.katan.service.account

open class AccountException : RuntimeException()

class AccountConflictException : AccountException()
