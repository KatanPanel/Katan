package me.devnatan.katan.core.server.compositions

import me.devnatan.katan.api.InternalKatanAPI
import me.devnatan.katan.api.server.*
import me.devnatan.katan.core.KatanCore
import me.devnatan.katan.core.server.compositions.compose.DockerComposeComposition
import me.devnatan.katan.core.server.compositions.compose.DockerComposeOptions
import me.devnatan.katan.core.server.compositions.image.DockerImageComposition
import me.devnatan.katan.core.server.compositions.image.DockerImageOptions

class DockerCompositionFactory(val core: KatanCore) :
    AbstractServerCompositionFactory(DockerComposeComposition, DockerImageComposition) {

    @InternalKatanAPI
    override suspend fun create(
        key: ServerComposition.Key<*>,
        server: Server,
        options: ServerCompositionOptions
    ): ServerComposition<*> {
        return when (key) {
            DockerComposeComposition -> {
                DockerComposeComposition(
                    if (options is ServerCompositionOptions.CLI) {
                        val compose = prompt("Enter the composition file name", "default")

                        message("Environment variables are separated by a white space (\" \") and values with a colon (:).")
                        val properties = prompt("Enter the environment variables", "").split(" ").map {
                            val (prop, value) = it.split(":")
                            prop to value
                        }.toMap()

                        DockerComposeOptions(compose, properties)
                    } else options as DockerComposeOptions, this
                )
            }
            DockerImageComposition -> {
                require(options is DockerImageOptions)

                val image = prompt("Enter the name of the Docker image")
                options.image = image
                DockerImageComposition(DockerImageOptions(image), this)
            }
            else -> throw IllegalArgumentException("Key $key is not supported")
        }
    }

}