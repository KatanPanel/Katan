package me.devnatan.katan.core.server.composition

import me.devnatan.katan.api.server.*

object DockerImageFactory : ServerCompositionFactory {

    override val adapter: ServerCompositionOptions.Adapter = DockerImageAdapter
    override val applicable: Array<out ServerComposition.Key<*>> = arrayOf(DockerImageComposition)
    override val lazy: Boolean = true

    override fun create(
        key: ServerComposition.Key<*>,
        server: Server
    ): ServerComposition<*> {
        return when (key) {
            DockerImageComposition -> DockerImageComposition()
            else -> throw IllegalArgumentException("Key $key isn't supported")
        }
    }

}

internal object DockerImageAdapter : ServerCompositionOptions.Adapter() {

    override suspend fun apply(key: ServerComposition.Key<*>): ServerCompositionOptions {
        val image = prompt("Enter the name of the image")
        return DockerImageOptions(image)
    }

}
