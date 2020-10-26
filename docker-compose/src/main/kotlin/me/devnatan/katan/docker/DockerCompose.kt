package me.devnatan.katan.docker

import me.devnatan.katan.api.Platform
import me.devnatan.katan.api.isWindows
import org.slf4j.Logger
import org.zeroturnaround.exec.ProcessExecutor
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream
import java.io.File
import java.nio.file.Paths

class DockerCompose(platform: Platform, logger: Logger) {

    companion object {

        /* Environment variables */
        const val COMPOSE_FILE = "COMPOSE_FILE"
        const val COMPOSE_PROJECT = "COMPOSE_PROJECT_NAME"

    }

    private val outputStream = Slf4jStream.of(logger).asInfo()
    private val errorStream = Slf4jStream.of(logger).asError()

    private lateinit var executable: File

    init {
        val target = if (platform.isWindows())
            "docker-compose.exe"
        else
            "docker-compose"

        // search the executable
        for (path in System.getenv("PATH").split(File.pathSeparatorChar).map { Paths.get(it) }) {
            val file = path.resolve(target).toFile()
            if (file.exists() && file.canExecute() && !file.isDirectory) {
                executable = file
                break
            }
        }

        if (!::executable.isInitialized)
            throw IllegalArgumentException("Unable to find Docker Compose executable in the system path.")
    }

    /**
     * Run a command in the Docker Compose CLI.
     * @param command the command to be executed
     * @param environment Docker Compose environment variables
     * @return command execution result.
     */
    fun runCommand(
        command: String,
        environment: Map<String, String> = emptyMap(),
        showOutput: Boolean = true,
        showErrors: Boolean = true,
        workingDir: String = executable.parent
    ): String {
        return ProcessExecutor()
            .command((listOf(executable.absolutePath) + command.split(" ")).filter { it.isNotBlank() })
            .environment(environment)
            .directory(File(workingDir))
            .readOutput(true)
            .exitValueNormal()
            .apply {
                if (showOutput) redirectOutput(outputStream)
                if (showErrors) redirectError(errorStream)
            }
            .execute().outputUTF8()
    }

}