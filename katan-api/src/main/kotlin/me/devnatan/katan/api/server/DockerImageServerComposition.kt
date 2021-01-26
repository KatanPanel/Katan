package me.devnatan.katan.api.server

interface DockerImageServerComposition : ServerComposition<DockerImageServerComposition.Options> {

    companion object Key : ServerComposition.Key<DockerImageServerComposition> {

        override fun toString(): String {
            return "Docker Image Server Composition"
        }

    }

    data class Options(
        val host: String,
        val port: Int,
        val memory: Long,
        val image: String,
        var environment: Map<String, Any>
    ) : ServerCompositionOptions

}