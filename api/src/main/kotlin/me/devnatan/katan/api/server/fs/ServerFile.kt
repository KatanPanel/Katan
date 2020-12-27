package me.devnatan.katan.api.server.fs

import me.devnatan.katan.api.account.Account
import java.io.File
import java.time.Instant

interface ServerFile {

    val file: File

    val isProtected: Boolean

    val lastModifiedAt: Instant

    val lastModifiedBy: Account

}