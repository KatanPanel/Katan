package org.katan.service.blueprint.http.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.katan.model.blueprint.BlueprintSpec
import org.katan.model.blueprint.BlueprintSpecBuild
import org.katan.model.blueprint.BlueprintSpecBuildImage
import org.katan.model.blueprint.BlueprintSpecBuildInstance
import org.katan.model.blueprint.BlueprintSpecOption
import org.katan.model.blueprint.BlueprintSpecRemote

@Serializable
internal data class BlueprintSpecResponse(
    val name: String,
    val version: String,
    val remote: BlueprintSpecRemoteResponse,
    val build: BlueprintSpecBuildResponse,
    val options: List<BlueprintSpecOptionResponse>
) {

    internal constructor(spec: BlueprintSpec) : this(
        name = spec.name,
        version = spec.version,
        remote = BlueprintSpecRemoteResponse(spec.remote),
        build = BlueprintSpecBuildResponse(spec.build),
        options = spec.options.map(::BlueprintSpecOptionResponse)
    )
}

@Serializable
internal data class BlueprintSpecOptionResponse(
    val name: String,
    val type: List<String>,
    val env: String?,
    val defaultValue: String?
) {

    internal constructor(option: BlueprintSpecOption) : this(
        name = option.name,
        type = option.type,
        env = option.env,
        defaultValue = option.defaultValue
    )
}

@Serializable
internal data class BlueprintSpecRemoteResponse(
    val origin: String
) {

    internal constructor(remote: BlueprintSpecRemote) : this(
        origin = remote.origin
    )
}

@Serializable
internal data class BlueprintSpecBuildResponse(
    val image: BlueprintSpecBuildImageResponse,
    val entrypoint: String,
    val env: Map<String, String>,
    val instance: BlueprintSpecBuildInstanceResponse?
) {

    internal constructor(build: BlueprintSpecBuild) : this(
        image = BlueprintSpecBuildImageResponse(build.image),
        entrypoint = build.entrypoint,
        env = build.env,
        instance = build.instance?.let(::BlueprintSpecBuildInstanceResponse)
    )
}

@Serializable
internal data class BlueprintSpecBuildInstanceResponse(val name: String? = null) {

    internal constructor(instance: BlueprintSpecBuildInstance) : this(
        name = instance.name
    )
}

@Serializable
internal sealed class BlueprintSpecBuildImageResponse {

    companion object {
        operator fun invoke(value: BlueprintSpecBuildImage): BlueprintSpecBuildImageResponse {
            return when (value) {
                is BlueprintSpecBuildImage.Single -> Single(value.id)
                is BlueprintSpecBuildImage.Ref -> Ref(
                    ref = value.ref,
                    tag = value.tag
                )
                is BlueprintSpecBuildImage.Multiple -> Multiple(
                    images = value.images
                )
                else -> error("Type not mapped")
            }
        }
    }

    @Serializable
    @SerialName("single")
    internal data class Single(override val id: String) :
        BlueprintSpecBuildImageResponse(),
        BlueprintSpecBuildImage.Single

    @Serializable
    @SerialName("ref")
    internal data class Ref(
        override val ref: String,
        override val tag: String
    ) : BlueprintSpecBuildImageResponse(), BlueprintSpecBuildImage.Ref

    @Serializable
    @SerialName("multiple")
    internal data class Multiple(
        override val images: List<BlueprintSpecBuildImage.Single>
    ) : BlueprintSpecBuildImageResponse(), BlueprintSpecBuildImage.Multiple
}
