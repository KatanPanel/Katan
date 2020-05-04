package me.devnatan.katan

import com.guichaguri.minimalftp.FTPConnection
import com.guichaguri.minimalftp.FTPServer
import com.guichaguri.minimalftp.api.IFTPListener
import com.guichaguri.minimalftp.api.IFileSystem
import com.guichaguri.minimalftp.impl.NativeFileSystem
import com.guichaguri.minimalftp.impl.NoOpAuthenticator
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.TimeUnit

class KatanFS(
    root: File
) : IFileSystem<File> by NativeFileSystem(root), IFTPListener {

    private companion object {
        val logger = LoggerFactory.getLogger(KatanFS::class.java)!!
    }

    private val server = FTPServer(NoOpAuthenticator(this))

    init {
        server.addListener(this)
        server.setTimeout(TimeUnit.SECONDS.toMillis(5).toInt())
        server.setBufferSize(1024 * 5)
    }

    internal fun listen(port: Int) {
        server.listen(port)
        logger.info("Listening file system on port $port.")
    }

    override fun onConnected(connection: FTPConnection) {
        logger.info("Connected: ${connection.address}")
    }

    override fun onDisconnected(connection: FTPConnection) {
        logger.info("Disconnected: ${connection.address}")
    }

}