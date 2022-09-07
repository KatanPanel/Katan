package org.katan.service.blueprint.model

import kotlinx.serialization.Serializable
import org.katan.model.blueprint.RawBlueprint
import org.katan.model.blueprint.RawBlueprintBuild
import org.katan.model.blueprint.RawBlueprintInstanceSettings
import org.katan.model.blueprint.RawBlueprintRemote

@Serializable
internal data class RawBlueprintImpl(
    override val name: String,
    override val version: String,
    override val icon: String?,
    override val type: String,
    override val remote: RawBlueprintRemoteImpl,
    override val build: RawBlueprintBuildImpl,
    override val instance: RawBlueprintInstanceSettingsImpl? = null
) : RawBlueprint

@Serializable
internal data class RawBlueprintRemoteImpl(
    override val main: String,
    override val origin: String? = null,
    override val provider: String? = null
) : RawBlueprintRemote

@Serializable
internal data class RawBlueprintBuildImpl(
    override val image: String,
    override val entrypoint: String,
    override val env: Map<String, String>? = null
) : RawBlueprintBuild

@Serializable
internal data class RawBlueprintInstanceSettingsImpl(
    override val name: String? = null
) : RawBlueprintInstanceSettings