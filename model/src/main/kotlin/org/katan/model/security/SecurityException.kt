package org.katan.model.security

open class SecurityException : RuntimeException()

class InvalidCredentialsException : SecurityException()