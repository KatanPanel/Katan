package me.devnatan.katan.core.server.compositions.compose

import de.gesellix.docker.compose.ComposeFileReader
import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.server.Server
import me.devnatan.katan.api.server.ServerComposition
import me.devnatan.katan.api.server.ServerCompositionFactory
import me.devnatan.katan.core.manager.DockerServerManager
import me.devnatan.katan.core.server.DockerServerContainer
import me.devnatan.katan.core.server.ServerImpl
import me.devnatan.katan.core.server.compositions.DockerCompositionFactory
import me.devnatan.katan.docker.DockerCompose
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

@OptIn(UnstableKatanApi::class)
class DockerComposeComposition(
    override val factory: ServerCompositionFactory,
    override val options: DockerComposeOptions
) : ServerComposition<DockerComposeOptions> {

    companion object Key : ServerComposition.Key<DockerComposeComposition> {

        private val logger: Logger = LoggerFactory.getLogger(DockerComposeComposition::class.java)

    }

    override val key: ServerComposition.Key<*> get() = Key

    override suspend fun read(server: Server) {}

    override suspend fun write(server: Server) {
        val katan = (factory as DockerCompositionFactory).core
        val compose = options.compose

        val pwd = File(DockerServerManager.COMPOSE_ROOT + File.separator + compose)
        if (!pwd.exists())
            pwd.mkdirs()

        val composeFile = File(pwd, "docker-compose.yml")
        if (!composeFile.exists())
            throw FileNotFoundException("Couldn't find Docker Compose for ${server.name} @ ${composeFile.absolutePath}")

        val config = ComposeFileReader().load(
            FileInputStream(composeFile), pwd.absolutePath, emptyMap()
        )!!

        val environmentArgs = options.properties.map { (key, value) ->
            "-e $key=$value"
        }.joinToString(" ")

        val containerName = DockerServerManager.CONTAINER_NAME_PATTERN.format(server.id.toString())
        for ((serviceName, _) in config.services ?: emptyMap()) {
            logger.info("Building service \"$serviceName\"...")

            katan.serverManager.composer.runCommand(
                "run -d --name $containerName $environmentArgs $serviceName", mapOf(
                    DockerCompose.COMPOSE_FILE to composeFile.absolutePath,
                    DockerCompose.COMPOSE_PROJECT to containerName
                ), showOutput = false, showErrors = false
            )
        }

        (server as ServerImpl).container = DockerServerContainer(containerName, katan.docker)
    }

}