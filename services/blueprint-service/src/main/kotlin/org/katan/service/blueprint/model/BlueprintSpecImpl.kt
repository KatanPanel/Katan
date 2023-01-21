package org.katan.service.blueprint.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.model.blueprint.BlueprintSpec
import org.katan.model.blueprint.BlueprintSpecBuild
import org.katan.model.blueprint.BlueprintSpecImage
import org.katan.model.blueprint.BlueprintSpecInstance
import org.katan.model.blueprint.BlueprintSpecOption
import org.katan.model.blueprint.BlueprintSpecRemote

@Serializable
internal data class BlueprintSpecImpl(
    override val name: String,
    override val version: String,
    override val remote: BlueprintSpecRemoteImpl?,
    override val build: BlueprintSpecBuildImpl?,
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
    override val image: BlueprintSpecImageImpl,
    override val entrypoint: String,
    override val env: Map<String, String>,
    override val instance: BlueprintSpecInstanceImpl?
) : BlueprintSpecBuild

@Serializable
internal sealed class BlueprintSpecImageImpl : BlueprintSpecImage {
    @Serializable
    @SerialName("identifier")
    data class Identifier(override val id: String) : BlueprintSpecImage.Identifier,
        BlueprintSpecImageImpl()

    @Serializable
    @SerialName("ref")
    data class Ref(override val ref: String, override val tag: String) : BlueprintSpecImage.Ref,
        BlueprintSpecImageImpl()

    @Serializable
    @SerialName("multiple")
    data class Multiple(override val images: List<Ref>) : BlueprintSpecImage.Multiple,
        BlueprintSpecImageImpl()
}

@Serializable
internal data class BlueprintSpecInstanceImpl(
    override val name: String
) : BlueprintSpecInstance
