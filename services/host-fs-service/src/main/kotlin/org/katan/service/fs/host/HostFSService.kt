package org.katan.service.fs.host

import com.github.dockerjava.api.DockerClient
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.model.fs.Bucket
import org.katan.model.fs.BucketNotFoundException
import org.katan.model.fs.VirtualFile
import org.katan.service.fs.FSService
import org.katan.service.fs.impl.BucketImpl
import org.katan.service.fs.impl.FileImpl
import java.io.File
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.attribute.FileTime

internal class HostFSService(
    private val dockerClient: DockerClient
) : FSService {

    companion object {
        private val logger: Logger = LogManager.getLogger(HostFSService::class.java)
    }

    override suspend fun getBucket(path: String): Bucket? {
        val id = retrieveDir(path) ?: return null
        val volume = withContext(IO) {
            dockerClient.inspectVolumeCmd(id).exec()
        }

        return BucketImpl(
            path = volume.mountpoint,
            name = volume.name,
            isLocal = volume.driver == "local",
            createdAt = (volume.rawValues["CreatedAt"] as? String)?.let { Instant.parse(it) }
        )
    }

    override suspend fun getFile(path: String): VirtualFile? {
        val dir = retrieveDir(path) ?: return null
        val fileName = retrieveFile(path) ?: return null

        logger.info("dir: $dir, fileName: $fileName")
        val volume = withContext(IO) {
            dockerClient.inspectVolumeCmd(dir).exec()
        } ?: throw BucketNotFoundException()

        val file = File(volume.mountpoint, fileName)
        logger.info("file: $file, ${file.exists()}")

        if (!file.exists()) {
            return null
        }

        val absPath = file.toPath()
        val modifiedAt = runCatching {
            Files.getLastModifiedTime(absPath, LinkOption.NOFOLLOW_LINKS)
        }.getOrNull()?.toInstant()?.toKotlinInstant()

        val createdAt = runCatching {
            Files.getAttribute(absPath, "creationTime") as? FileTime
        }.getOrNull()?.toInstant()?.toKotlinInstant() ?: modifiedAt

        return FileImpl(
            name = file.name,
            absolutePath = file.absolutePath,
            size = file.length(),
            isDirectory = file.isDirectory,
            createdAt = createdAt ?: modifiedAt,
            modifiedAt = modifiedAt
        )
    }

    private fun retrieveDir(path: String): String? {
        return path.substringBefore("/", "").ifEmpty { null }
    }

    private fun retrieveFile(path: String): String? {
        return path.substringAfterLast("/").ifEmpty { null }
    }
}
