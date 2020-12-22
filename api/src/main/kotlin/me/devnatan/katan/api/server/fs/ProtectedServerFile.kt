package me.devnatan.katan.api.server.fs

import me.devnatan.katan.api.security.credentials.Credentials

interface ProtectedServerFile : ServerFile {

    override val isProtected: Boolean
        get() = true

    val credentials: Credentials

}