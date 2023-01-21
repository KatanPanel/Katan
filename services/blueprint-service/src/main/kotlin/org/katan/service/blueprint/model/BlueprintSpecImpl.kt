package org.katan.service.blueprint.model

import kotlinx.serialization.Serializable
import org.katan.model.blueprint.BlueprintSpec
import org.katan.model.blueprint.BlueprintSpecBuild
import org.katan.model.blueprint.BlueprintSpecImage
import org.katan.model.blueprint.BlueprintSpecInstance
import org.katan.model.blueprint.BlueprintSpecOption
import org.katan.model.blueprint.BlueprintSpecOptions
import org.katan.model.blueprint.BlueprintSpecRemote

@Serializable
internal data class BlueprintSpecImpl(
    override val name: String = "",
    override val version: String = "",
    override val remote: BlueprintSpecRemote = BlueprintSpecRemoteImpl(),
    override val build: BlueprintSpecBuild = BlueprintSpecBuildImpl(),
    override val options: BlueprintSpecOptions = BlueprintSpecOptions()
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
internal data class BlueprintSpecRemoteImpl(override val origin: String = "") : BlueprintSpecRemote

@Serializable
internal data class BlueprintSpecBuildImpl(
    override val image: BlueprintSpecImage = BlueprintSpecImageImpl.Identifier(""),
    override val entrypoint: String = "",
    override val env: Map<String, String> = emptyMap(),
    override val instance: BlueprintSpecInstanceImpl? = null
) : BlueprintSpecBuild

@Serializable
internal sealed class BlueprintSpecImageImpl {
    @Serializable
    data class Identifier(override val id: String) :
        BlueprintSpecImageImpl(), BlueprintSpecImage.Identifier

    @Serializable
    data class Ref(override val ref: String, override val tag: String) :
        BlueprintSpecImageImpl(), BlueprintSpecImage.Ref

    @Serializable
    data class Multiple(override val images: List<Ref>) :
        BlueprintSpecImageImpl(), BlueprintSpecImage.Multiple
}

@Serializable
internal data class BlueprintSpecInstanceImpl(override val name: String? = null) : BlueprintSpecInstance
