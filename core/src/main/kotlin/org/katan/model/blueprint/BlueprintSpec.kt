package org.katan.model.blueprint

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BlueprintSpec(
    val name: String,
    val version: String,
    val remote: BlueprintSpecRemote?,
    val build: BlueprintSpecBuild?,
    val options: List<BlueprintSpecOption> = emptyList(),
)

@Serializable
data class BlueprintSpecOption(
    val id: String,
    val name: String,
    val type: List<String>,
    val env: String?,
    val defaultValue: String,
)

@Serializable
data class BlueprintSpecRemote(
    val origin: String,
)

@Serializable
data class BlueprintSpecBuild(
    val image: BlueprintSpecImage,
    val entrypoint: String,
    val env: Map<String, String>,
    val instance: BlueprintSpecInstance?,
)

@Serializable
sealed class BlueprintSpecImage {
    @Serializable
    @SerialName("identifier")
    data class Identifier(val id: String) : BlueprintSpecImage()

    @Serializable
    @SerialName("ref")
    data class Ref(val ref: String, val tag: String) : BlueprintSpecImage()

    @Serializable
    @SerialName("multiple")
    data class Multiple(val images: List<Ref>) : BlueprintSpecImage()
}

@Serializable
data class BlueprintSpecInstance(
    val name: String,
)
