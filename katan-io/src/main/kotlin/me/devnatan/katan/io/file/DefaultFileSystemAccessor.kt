package me.devnatan.katan.io.file

import com.typesafe.config.Config
import me.devnatan.katan.api.Descriptor
import me.devnatan.katan.api.io.FileSystemAccessor
import me.devnatan.katan.api.io.FileSystemSession
import me.devnatan.katan.api.io.UnauthorizedFileSystemAccessException
import me.devnatan.katan.api.logging.logger
import me.devnatan.katan.common.util.get
import me.devnatan.katan.io.file.session.FileSystemSessionImpl
import org.slf4j.Logger

class DefaultFileSystemAccessor(@JvmField val config: Config, private val fs: PersistentFileSystem) : FileSystemAccessor {

    private companion object {

        val logger: Logger = logger<DefaultFileSystemAccessor>()

    }

    override suspend fun newSession(holder: Descriptor): FileSystemSession {
        if (!config.get("security.file-system.allow-untrusted-access", false))
            throw UnauthorizedFileSystemAccessException(holder.name)

        val session = FileSystemSessionImpl(holder, fs)
        logger.info("Session $session opened.")

        return session
    }

}