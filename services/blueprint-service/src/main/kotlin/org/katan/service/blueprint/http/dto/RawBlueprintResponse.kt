package org.katan.service.blueprint.http.dto

import kotlinx.serialization.Serializable
import org.katan.model.blueprint.RawBlueprint
import org.katan.model.blueprint.RawBlueprintBuild
import org.katan.model.blueprint.RawBlueprintInstanceSettings
import org.katan.model.blueprint.RawBlueprintRemote
import org.katan.model.blueprint.normalizedIcon

@Serializable
internal data class RawBlueprintResponse(
    val name: String,
    val version: String,
    val type: String,
    val icon: String?,
    val remote: RawBlueprintRemoteResponse,
    val build: RawBlueprintBuildResponse,
    val instance: RawBlueprintInstanceSettingsResponse?
) {

    internal constructor(raw: RawBlueprint) : this(
        name = raw.name,
        version = raw.version,
        type = raw.type,
        icon = raw.normalizedIcon(),
        remote = RawBlueprintRemoteResponse(raw.remote),
        build = RawBlueprintBuildResponse(raw.build),
        instance = raw.instance?.let(::RawBlueprintInstanceSettingsResponse)
    )

}

@Serializable
internal data class RawBlueprintRemoteResponse(
    val main: String,
    val origin: String?,
    val provider: String?
) {

    internal constructor(remote: RawBlueprintRemote) : this(
        main = remote.main,
        origin = remote.origin,
        provider = remote.provider
    )

}

@Serializable
internal data class RawBlueprintBuildResponse(
    val image: String,
    val env: Map<String, String>?
) {

    internal constructor(build: RawBlueprintBuild) : this(
        image = build.image,
        env = build.env
    )

}

@Serializable
internal data class RawBlueprintInstanceSettingsResponse(
    val name: String?
) {

    internal constructor(settings: RawBlueprintInstanceSettings) : this(
        name = settings.name
    )

}