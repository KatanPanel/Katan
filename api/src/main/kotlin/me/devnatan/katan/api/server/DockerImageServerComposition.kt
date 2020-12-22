package me.devnatan.katan.api.server

interface DockerImageServerComposition : ServerComposition<DockerImageServerComposition.Options> {

    companion object Key : ServerComposition.Key<DockerImageServerComposition>

    data class Options(
        val host: String,
        val port: Int,
        val memory: Long,
        val image: String,
        var environment: Map<String, Any>
    ) : ServerCompositionOptions

}