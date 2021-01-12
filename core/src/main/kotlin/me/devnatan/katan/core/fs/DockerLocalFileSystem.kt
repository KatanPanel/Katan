package me.devnatan.katan.core.fs

import com.github.dockerjava.api.command.InspectContainerResponse
import me.devnatan.katan.api.io.FileDisk
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.impl.server.DockerServerContainer
import me.devnatan.katan.core.impl.server.DockerServerContainerInspection
import me.devnatan.katan.fs.FileSystem
import java.io.FileNotFoundException
import kotlin.contracts.ExperimentalContracts

class DockerLocalFileSystem(
    private val core: KatanCore
) : FileSystem {

    private companion object {
        const val DEFAULT_FILE_DISK_KIND = "volume"
    }

    @OptIn(ExperimentalContracts::class)
    override suspend fun listDisks(server: Server): List<FileDisk> {
        ensureInspected(server)

        val container = server.container as DockerServerContainer
        val disks = mutableListOf<FileDisk>()

        for (mount in (container.inspection as DockerServerContainerInspection).response.mounts!!) {
            disks.add(mountToDisk(mount))
        }

        return disks
    }

    private suspend fun mountToDisk(mount: InspectContainerResponse.Mount): FileDisk {
        val name = mount.name!!

        // TODO: suspend this
        val volume = core.docker.client.inspectVolumeCmd(name).exec()

        val file = java.io.File(volume.mountpoint)
        if (!file.exists())
            throw FileNotFoundException(name)

        return DockerFileDisk(name, volume.mountpoint, file.length(), DEFAULT_FILE_DISK_KIND, null)
    }

    private suspend fun ensureInspected(server: Server) {
        // we need to ensure that the inspection is up to date
        // so that the data is always synchronized.
        core.serverManager.inspectServer(server)

        check(server.container.isInspected()) { "Server must be inspected." }
        require(server.container is DockerServerContainer) { "Non-Docker containers aren't yet supported." }
    }

}