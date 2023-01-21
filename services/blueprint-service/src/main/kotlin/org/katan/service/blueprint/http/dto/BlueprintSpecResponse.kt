package org.katan.service.blueprint.http.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.katan.model.blueprint.BlueprintSpec
import org.katan.model.blueprint.BlueprintSpecBuild
import org.katan.model.blueprint.BlueprintSpecImage
import org.katan.model.blueprint.BlueprintSpecInstance
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
    val image: @Contextual Any?,
    val entrypoint: String,
    val env: Map<String, String>,
    val instance: BlueprintSpecInstanceResponse?
) {

    internal constructor(build: BlueprintSpecBuild) : this(
        image = when (val image = build.image) {
            is BlueprintSpecImage.Identifier -> BlueprintSpecImageIdentifierResponse(image.id)
            is BlueprintSpecImage.Ref -> BlueprintSpecImageRefResponse(
                ref = image.ref,
                tag = image.tag
            )
            is BlueprintSpecImage.Multiple -> image.images.map { image ->
                BlueprintSpecImageRefResponse(
                    ref = image.ref,
                    tag = image.tag
                )
            }
        },
        entrypoint = build.entrypoint,
        env = build.env,
        instance = build.instance?.let(::BlueprintSpecInstanceResponse)
    )
}

@Serializable
internal data class BlueprintSpecInstanceResponse(val name: String? = null) {

    internal constructor(instance: BlueprintSpecInstance) : this(
        name = instance.name
    )
}

@Serializable
internal data class BlueprintSpecImageIdentifierResponse(val id: String)

@Serializable
internal data class BlueprintSpecImageRefResponse(val ref: String, val tag: String)
