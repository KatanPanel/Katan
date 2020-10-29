package me.devnatan.katan.core.impl.server.compositions

import me.devnatan.katan.api.annotations.InternalKatanApi
import me.devnatan.katan.api.annotations.UnstableKatanApi
import me.devnatan.katan.api.server.ServerComposition
import me.devnatan.katan.api.server.ServerCompositionOptions
import me.devnatan.katan.api.server.addSupportedKey
import me.devnatan.katan.api.server.prompt
import me.devnatan.katan.common.impl.game.GameImageImpl
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.impl.server.compositions.compose.DockerComposeComposition
import me.devnatan.katan.core.impl.server.compositions.compose.DockerComposeOptions
import me.devnatan.katan.core.impl.server.compositions.image.DockerImageComposition
import me.devnatan.katan.core.impl.server.compositions.image.DockerImageOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@OptIn(UnstableKatanApi::class)
class DockerCompositionFactory(val core: KatanCore) : InvisibleCompositionFactory() {

    init {
        addSupportedKey("docker-image", DockerImageComposition)
        addSupportedKey("docker-compose", DockerComposeComposition)
    }

    private val logger: Logger = LoggerFactory.getLogger(DockerCompositionFactory::class.java)

    @InternalKatanApi
    override suspend fun create(
        key: ServerComposition.Key<*>,
        options: ServerCompositionOptions
    ): ServerComposition<*> {
        return when (key) {
            is DockerImageComposition.Key -> DockerImageComposition(this, options as DockerImageOptions)
            is DockerComposeComposition.Key -> DockerComposeComposition(
                this,
                if (options !is ServerCompositionOptions.CLI)
                    options as DockerComposeOptions
                else {
                    val compose = prompt("Enter the composition file name", "default")

                    logger.info("Environment variables are separated by a white space (\" \") and values with a colon (:).")
                    val properties = prompt("Enter the environment variables", "")
                        .split(" ").map {
                            val (prop, value) = it.split(":")
                            prop to value
                        }.toMap()

                    DockerComposeOptions(compose, properties)
                }
            )
            else -> throw IllegalArgumentException(key.toString())
        }
    }

    override suspend fun generate(key: ServerComposition.Key<*>, data: Map<String, Any>): ServerCompositionOptions {
        return when (key) {
            is DockerImageComposition.Key -> DockerImageOptions(
                data.getValue("host") as String,
                (data.getValue("port") as Long).toInt(),
                data.getValue("memory") as Long,
                (data.getValue("image") as Map<String, Any>).let {
                    GameImageImpl(it["id"] as String, it["environment"] as Map<String, Any>)
                }
            )
            is DockerComposeComposition.Key -> DockerComposeOptions(
                data.getValue("compose") as String,
                data.getValue("properties") as Map<String, String>
            )
            else -> throw IllegalArgumentException(key.toString())
        }
    }

}