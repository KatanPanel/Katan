package me.devnatan.katan.io.file

import com.github.dockerjava.api.command.InspectContainerResponse
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.devnatan.katan.api.io.File
import me.devnatan.katan.api.io.FileDisk
import me.devnatan.katan.api.io.FileOrigin
import me.devnatan.katan.api.io.FileSystemSession
import me.devnatan.katan.api.logging.logger
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.impl.server.DockerServerContainer
import me.devnatan.katan.core.impl.server.DockerServerContainerInspection
import me.devnatan.katan.io.file.disk.DockerHostFileDisk
import org.slf4j.Logger
import java.io.FileNotFoundException
import java.time.Instant
import java.util.*
import kotlin.contracts.ExperimentalContracts

class DockerHostFileSystem(
    private val core: KatanCore
) : PersistentFileSystem {

    companion object {

        val DEFAULT_FILE_SYSTEM_ORIGIN = FileOrigin.LOCAL
        private val logger: Logger = logger<DockerHostFileSystem>()

    }

    private val activeSessions = mutableMapOf<UUID, FileSystemSession>()
    private val lock = Mutex()
    private var closed = false

    override suspend fun isProtected(file: File): Boolean {
        TODO("Not yet implemented.")
    }

    override suspend fun getDisk(server: Server, id: String): FileDisk? {
        checkAvailability()
        ensureInspected(server)

        val container = server.container as DockerServerContainer
        return (container.inspection as DockerServerContainerInspection)
            .response.mounts!!.firstOrNull { it.name == id }
            ?.let { mountToDisk(it) }
    }

    @OptIn(ExperimentalContracts::class)
    override suspend fun listDisks(server: Server): List<FileDisk> {
        checkAvailability()
        ensureInspected(server)

        val container = server.container as DockerServerContainer
        val disks = mutableListOf<FileDisk>()

        for (mount in (container.inspection as DockerServerContainerInspection).response.mounts!!) {
            disks.add(mountToDisk(mount))
        }

        return disks
    }

    private fun mountToDisk(mount: InspectContainerResponse.Mount): FileDisk {
        checkAvailability()

        val name = mount.name!!
        val volume = mount.destination!!

        val file = java.io.File(mount.source!!)
        if (!file.exists())
            throw FileNotFoundException("$name @ ${volume.path}")

        return DockerHostFileDisk(
            name,
            file.path,
            file.length(),
            DEFAULT_FILE_SYSTEM_ORIGIN,
            null,
            Instant.ofEpochMilli(file.lastModified()),
            this
        )
    }

    private suspend fun ensureInspected(server: Server) {
        require(server.container is DockerServerContainer) { "Non-Docker containers aren't yet supported." }

        // we need to ensure that the inspection is up to date
        // so that the data is always synchronized.
        if (!server.container.isInspected())
            core.serverManager.inspectServer(server)

        check(server.container.isInspected()) { "Server must be inspected." }
    }

    fun checkAvailability() {
        check(!closed) { "FileSystem is closed." }
    }

    override suspend fun close() {
        lock.lock()
        try {
            val iterator = activeSessions.iterator()
            while (iterator.hasNext()) {
                close0(iterator.next().value)
                iterator.remove()
            }
        } finally {
            lock.unlock()
        }
    }

    private suspend fun close0(session: FileSystemSession) {
        logger.info("Session $session closed.")

        // clears resources from the session itself
        session.close()
    }

    override suspend fun close(session: FileSystemSession) {
        // it is necessary to notify open sessions that the
        // session is being closed so that there is no de-synchronization.

        // TODO: internally notify session close

        lock.withLock(activeSessions) {
            activeSessions.remove(session.uid)
        }

        close0(session)
    }

    override suspend fun open(session: FileSystemSession) {
        checkAvailability()
        check(!session.isClosed()) { "Session already open." }

        lock.withLock(activeSessions) {
            activeSessions[session.uid] = session
        }
    }

}