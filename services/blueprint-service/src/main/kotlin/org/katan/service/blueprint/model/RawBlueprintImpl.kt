package org.katan.service.blueprint.model

import kotlinx.serialization.Serializable
import org.katan.model.blueprint.RawBlueprint
import org.katan.model.blueprint.RawBlueprintBuild
import org.katan.model.blueprint.RawBlueprintInstance
import org.katan.model.blueprint.RawBlueprintOption
import org.katan.model.blueprint.RawBlueprintRemote

@Serializable
internal data class RawBlueprintImpl(
    override val name: String,
    override val version: String,
    override val icon: String?,
    override val remote: RawBlueprintRemoteImpl,
    override val build: RawBlueprintBuildImpl,
    override val options: List<RawBlueprintOptionImpl> = emptyList()
) : RawBlueprint

@Serializable
internal data class RawBlueprintOptionImpl(
    override val name: String,
    override val type: List<String>,
    override val env: String?,
    override val defaultValue: String
) : RawBlueprintOption

@Serializable
internal data class RawBlueprintRemoteImpl(
    override val main: String,
    override val origin: String,
    override val exports: List<String> = emptyList()
) : RawBlueprintRemote

@Serializable
internal data class RawBlueprintBuildImpl(
    override val image: String,
    override val entrypoint: String,
    override val env: Map<String, String> = emptyMap(),
    override val instance: RawBlueprintInstanceImpl? = null
) : RawBlueprintBuild

@Serializable
internal data class RawBlueprintInstanceImpl(
    override val name: String? = null
) : RawBlueprintInstance
