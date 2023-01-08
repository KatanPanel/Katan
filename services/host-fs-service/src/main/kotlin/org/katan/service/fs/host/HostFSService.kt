package org.katan.service.fs.host

import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.api.command.InspectVolumeResponse
import com.github.dockerjava.api.exception.DockerException
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.toKotlinInstant
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.katan.config.KatanConfig
import org.katan.model.io.Bucket
import org.katan.model.io.BucketNotFoundException
import org.katan.model.io.FileNotAccessibleException
import org.katan.model.io.FileNotFoundException
import org.katan.model.io.FileNotReadableException
import org.katan.model.io.FileNotWritableException
import org.katan.model.io.VirtualFile
import org.katan.service.fs.FSService
import org.katan.service.fs.impl.BucketImpl
import org.katan.service.fs.impl.DirectoryImpl
import org.katan.service.fs.impl.FileImpl
import java.io.File
import java.nio.file.Files
import java.nio.file.LinkOption
import java.nio.file.attribute.FileTime
import kotlin.streams.asSequence
import kotlin.system.exitProcess

internal class HostFSService(
    private val dockerClient: DockerClient,
    private val config: KatanConfig
) : FSService {

    companion object {
        private val logger: Logger = LogManager.getLogger(HostFSService::class.java)
    }

    private val fsRoot: String

    init {
        val value: String? = findFsRoot()

        if (value == null) {
            // TODO provide better error message
            logger.error("Unable to determine file system root.")
            exitProcess(1)
        }

        fsRoot = value
    }

    private fun findFsRoot(): String? {
        val info = try {
            dockerClient.infoCmd().exec()
        } catch (e: DockerException) {
            logger.error("Failed to execute Docker info command to fetch file system root", e)
            exitProcess(1)
        }

        return info.dockerRootDir?.let {
            buildString {
                append(it)
                append(File.separatorChar)
                append("volumes")
            }
        }
    }

    override suspend fun getFile(bucket: String, destination: String, path: String): VirtualFile? {
        val volume = getVolumeOrNull(bucket) ?: throw BucketNotFoundException()

        val base = File(buildFile(volume.name, volume.mountpoint))
        val file = File(base, File.separator + path)

        if (!file.exists()) {
            logger.debug("File not found: $file")
            return null
        }

        if (file.isDirectory) {
            return file.toDomain(base, file.listFiles()?.map { it.toDomain(base) })
        }

        return file.toDomain(base)
    }

    override suspend fun readFile(
        path: String,
        startIndex: Int?,
        endIndex: Int?
    ): File {
        val file = File(path)
        if (!file.exists()) throw FileNotFoundException()
        if (!file.canRead()) throw FileNotReadableException()

        return file
    }

    override suspend fun readFile(bucket: String?, destination: String, name: String): Pair<VirtualFile, ByteArray> {
        if (!bucket.isNullOrBlank()) {
            throw IllegalStateException("Only local reads are supported for now")
        }

        val base = File(destination)
        val file = File(base, name)
        if (!file.exists()) throw FileNotFoundException()
        if (!file.canRead()) throw FileNotReadableException()

        val bytes = withContext(IO) {
            file.readBytes()
        }

        return file.toDomain(base) to bytes
    }

    override suspend fun getBucket(bucket: String, destination: String): Bucket? {
        val volume = getVolumeOrNull(bucket) ?: return null

        return BucketImpl(
            path = volume.mountpoint,
            name = volume.name,
            isLocal = volume.driver == "local",
            createdAt = (volume.rawValues["CreatedAt"] as? String)?.let { Instant.parse(it) }
        )
    }

    override suspend fun uploadFile(
        bucket: String?,
        destination: String,
        name: String,
        contents: ByteArray
    ): VirtualFile {
        if (!bucket.isNullOrBlank()) {
            throw IllegalStateException("Only local uploads are supported for now")
        }

        val base = File(destination)
        val file = File(base, name)
        logger.info("File: $file")
        base.mkdirs()

        withContext(IO) {
            if (!file.exists()) {
                file.createNewFile()
            }

            if (!file.canWrite()) {
                throw FileNotWritableException()
            }

            try {
                file.writeBytes(contents)
            } catch (e: SecurityException) {
                throw FileNotAccessibleException()
            }
        }

        return file.toDomain(base)
    }

    private suspend fun getVolumeOrNull(name: String): InspectVolumeResponse? {
        return withContext(IO) {
            runCatching {
                dockerClient.inspectVolumeCmd(name).exec()
            }.getOrNull()
        }
    }

    private fun buildFile(volumeName: String, mountpoint: String): String {
        return buildString {
            append(fsRoot)
            append(File.separatorChar)
            append(volumeName)
            append(File.separatorChar)
            append(mountpoint.substringAfterLast("/"))
        }
    }

    private fun File.toDomain(base: File, children: List<VirtualFile>? = null): VirtualFile {
        val absPath = toPath()
        val modifiedAt = runCatching {
            Files.getLastModifiedTime(absPath, LinkOption.NOFOLLOW_LINKS)
        }.getOrNull()?.toInstant()?.toKotlinInstant()

        val createdAt = runCatching {
            Files.getAttribute(absPath, "creationTime") as? FileTime
        }.getOrNull()?.toInstant()?.toKotlinInstant() ?: modifiedAt

        val size = if (!isDirectory) {
            length()
        } else {
            Files.walk(absPath).asSequence()
                .map { it.toFile() }
                .filter { it.isFile && it.exists() }
                .map { it.length() }
                .sum()
        }

        val file = FileImpl(
            name = name,
            relativePath = toRelativeStringOrEmpty(base),
            absolutePath = absolutePath,
            size = size,
            isDirectory = isDirectory,
            isHidden = isHidden,
            createdAt = createdAt ?: modifiedAt,
            modifiedAt = modifiedAt
        )
        if (children == null) {
            return file
        }

        return DirectoryImpl(file, children)
    }

    private fun File.toRelativeStringOrEmpty(base: File): String {
        return toRelativeString(base).let {
            if (it.equals(name, ignoreCase = false)) {
                ""
            } else {
                it.substringBeforeLast(File.separatorChar)
            }
        }
    }
}
