package org.katan.service.blueprint.http.dto

import kotlinx.serialization.Serializable
import org.katan.model.blueprint.BlueprintSpec
import org.katan.model.blueprint.BlueprintSpecBuild
import org.katan.model.blueprint.BlueprintSpecBuildInstance
import org.katan.model.blueprint.BlueprintSpecOption
import org.katan.model.blueprint.BlueprintSpecRemote

@Serializable
internal data class RawBlueprintResponse(
    val name: String,
    val version: String,
    val icon: String?,
    val remote: RawBlueprintRemoteResponse,
    val build: RawBlueprintBuildResponse,
    val options: List<RawBlueprintOptionResponse>
) {

    internal constructor(raw: BlueprintSpec) : this(
        name = raw.name,
        version = raw.version,
        icon = raw.icon,
        remote = RawBlueprintRemoteResponse(raw.remote),
        build = RawBlueprintBuildResponse(raw.build),
        options = raw.options.map(::RawBlueprintOptionResponse)
    )
}

@Serializable
internal data class RawBlueprintOptionResponse(
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
internal data class RawBlueprintRemoteResponse(
    val main: String,
    val origin: String,
    val exports: List<String>
) {

    internal constructor(remote: BlueprintSpecRemote) : this(
        main = remote.main,
        origin = remote.origin,
        exports = remote.exports
    )
}

@Serializable
internal data class RawBlueprintBuildResponse(
    val image: String,
    val entrypoint: String,
    val env: Map<String, String>,
    val instance: RawBlueprintInstanceResponse?
) {

    internal constructor(build: BlueprintSpecBuild) : this(
        image = build.image,
        entrypoint = build.entrypoint,
        env = build.env,
        instance = build.instance?.let(::RawBlueprintInstanceResponse)
    )
}

@Serializable
internal data class RawBlueprintInstanceResponse(
    val name: String? = null
) {

    internal constructor(instance: BlueprintSpecBuildInstance) : this(
        name = instance.name
    )
}
