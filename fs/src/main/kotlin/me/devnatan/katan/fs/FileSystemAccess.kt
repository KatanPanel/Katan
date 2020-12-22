package me.devnatan.katan.fs

import me.devnatan.katan.api.security.account.Account
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.core.impl.server.DockerServerContainerInspection
import java.io.File
import java.time.Instant

class FileSystemAccess(
    val account: Account,
    val loggedInAt: Instant
) {

    suspend fun list(server: Server): List<File> {
        check(server.container.isInspected()) { "Server not yet inspected." }

        val inspection = server.container.inspection
        require(inspection is DockerServerContainerInspection) { "Server must be a Docker server." }

        throw IllegalArgumentException()
    }

}