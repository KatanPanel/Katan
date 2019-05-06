package me.devnatan.katan.backend

import com.guichaguri.minimalftp.FTPConnection
import com.guichaguri.minimalftp.FTPServer
import com.guichaguri.minimalftp.api.IFTPListener
import com.guichaguri.minimalftp.api.IFileSystem
import com.guichaguri.minimalftp.impl.NativeFileSystem
import com.guichaguri.minimalftp.impl.NoOpAuthenticator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class KatanFTP : IFTPListener {

    companion object {

        private val LOGGER: Logger = LoggerFactory.getLogger("FTP")

    }

    lateinit var server: FTPServer
    lateinit var fs: IFileSystem<File>

    fun init() {
        fs = NativeFileSystem(File(System.getProperty("user.dir")))
        server = FTPServer(NoOpAuthenticator(fs))
        server.addListener(this)
        server.setTimeout(5 * 60 * 1000)
        server.setBufferSize(1024 * 5)
        server.listen(21)
        LOGGER.info("Listening on 21...")
    }

    fun listFiles(file: File): List<FTPFile> {
        return fs.listFiles(file).map {
            FTPFile(it)
        }
    }

    override fun onConnected(conn: FTPConnection) {
        LOGGER.info("New connection: ${conn.address}")
    }

    override fun onDisconnected(conn: FTPConnection) {
        LOGGER.info("Connection released: ${conn.address}")
    }

}

class FTPFile(file: File) {

    val name: String = file.name
    val path: String = file.canonicalPath
    val extension: String = file.extension
    val lastModified: Long = file.lastModified()
    val isDirectory: Boolean = file.isDirectory
    val isHidden: Boolean = file.isHidden
    val length: Long = file.length()

}