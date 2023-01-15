package org.katan.service.blueprint.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.model.blueprint.BlueprintSpec
import org.katan.model.blueprint.BlueprintSpecBuild
import org.katan.model.blueprint.BlueprintSpecBuildImage
import org.katan.model.blueprint.BlueprintSpecBuildInstance
import org.katan.model.blueprint.BlueprintSpecOption
import org.katan.model.blueprint.BlueprintSpecRemote

@Serializable
internal data class BlueprintSpecImpl(
    override val name: String,
    override val version: String,
    override val remote: BlueprintSpecRemoteImpl,
    override val build: BlueprintSpecBuildImpl,
    override val options: List<BlueprintSpecOptionImpl> = emptyList()
) : BlueprintSpec

@Serializable
internal data class BlueprintSpecOptionImpl(
    override val id: String,
    override val name: String,
    override val type: List<String>,
    override val env: String?,
    override val defaultValue: String
) : BlueprintSpecOption

@Serializable
internal data class BlueprintSpecRemoteImpl(
    override val origin: String
) : BlueprintSpecRemote

@Serializable
internal data class BlueprintSpecBuildImpl(
    override val image: BlueprintSpecBuildImageImpl,
    override val entrypoint: String,
    override val env: Map<String, String> = emptyMap(),
    override val instance: BlueprintSpecBuildInstanceImpl? = null
) : BlueprintSpecBuild

@Serializable
internal data class BlueprintSpecBuildInstanceImpl(
    override val name: String? = null
) : BlueprintSpecBuildInstance

@Serializable
internal sealed class BlueprintSpecBuildImageImpl : BlueprintSpecBuildImage {

    @JvmInline
    @Serializable
    @SerialName("single")
    internal value class Single(override val id: String) : BlueprintSpecBuildImage.Single

    @Serializable
    @SerialName("ref")
    internal data class Ref(
        override val ref: String,
        override val tag: String
    ) : BlueprintSpecBuildImage.Ref

    @Serializable
    @SerialName("multiple")
    internal data class Multiple(
        override val images: List<BlueprintSpecBuildImage.Single>
    ) : BlueprintSpecBuildImage.Multiple
}
