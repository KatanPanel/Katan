package org.katan.service.blueprint.http.dto

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
    val image: String?,
    val images: List<BlueprintSpecBuildImageResponse>?,
    val entrypoint: String,
    val env: Map<String, String>,
    val instance: BlueprintSpecBuildInstanceResponse?
) {

    internal constructor(build: BlueprintSpecBuild) : this(
        image = build.image,
        images = build.images?.map(::BlueprintSpecBuildImageResponse),
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
internal data class BlueprintSpecBuildImageResponse(
    val ref: String,
    val tag: String
) {

    internal constructor(image: BlueprintSpecBuildImage) : this(
        ref = image.ref,
        tag = image.tag
    )
}
