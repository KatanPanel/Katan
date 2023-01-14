package org.katan.service.blueprint.http.dto

import kotlinx.serialization.Serializable
import org.katan.model.blueprint.RawBlueprint
import org.katan.model.blueprint.RawBlueprintBuild
import org.katan.model.blueprint.RawBlueprintInstance
import org.katan.model.blueprint.RawBlueprintOption
import org.katan.model.blueprint.RawBlueprintRemote

@Serializable
internal data class RawBlueprintResponse(
    val name: String,
    val version: String,
    val icon: String?,
    val remote: RawBlueprintRemoteResponse,
    val build: RawBlueprintBuildResponse,
    val options: List<RawBlueprintOptionResponse>
) {

    internal constructor(raw: RawBlueprint) : this(
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

    internal constructor(option: RawBlueprintOption) : this(
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

    internal constructor(remote: RawBlueprintRemote) : this(
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

    internal constructor(build: RawBlueprintBuild) : this(
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

    internal constructor(instance: RawBlueprintInstance) : this(
        name = instance.name
    )
}
