package org.katan.service.blueprint.model

import kotlinx.serialization.Serializable
import org.katan.model.blueprint.BlueprintSpec
import org.katan.model.blueprint.BlueprintSpecBuild
import org.katan.model.blueprint.BlueprintSpecBuildInstance
import org.katan.model.blueprint.BlueprintSpecOption
import org.katan.model.blueprint.BlueprintSpecRemote

@Serializable
internal data class BlueprintSpecImpl(
    override val name: String,
    override val version: String,
    override val icon: String?,
    override val remote: BlueprintSpecRemoteImpl,
    override val build: BlueprintSpecBuildImpl,
    override val options: List<BlueprintSpecOptionImpl> = emptyList()
) : BlueprintSpec

@Serializable
internal data class BlueprintSpecOptionImpl(
    override val name: String,
    override val type: List<String>,
    override val env: String?,
    override val defaultValue: String
) : BlueprintSpecOption

@Serializable
internal data class BlueprintSpecRemoteImpl(
    override val main: String,
    override val origin: String,
    override val exports: List<String> = emptyList()
) : BlueprintSpecRemote

@Serializable
internal data class BlueprintSpecBuildImpl(
    override val image: String,
    override val entrypoint: String,
    override val env: Map<String, String> = emptyMap(),
    override val instance: BlueprintSpecBuildInstanceImpl? = null
) : BlueprintSpecBuild

@Serializable
internal data class BlueprintSpecBuildInstanceImpl(
    override val name: String? = null
) : BlueprintSpecBuildInstance
