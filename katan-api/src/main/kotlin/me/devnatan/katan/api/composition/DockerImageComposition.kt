package me.devnatan.katan.api.composition

interface DockerImageComposition : Composition<DockerImageComposition.Options> {

    companion object Key : Composition.Key by Composition.Key("Docker Image Server Composition")

    override val key: Composition.Key
        get() = Key

    data class Options(
        val host: String,
        val port: Int,
        val memory: Long,
        val image: String,
        var environment: Map<String, Any>
    ) : CompositionOptions

}