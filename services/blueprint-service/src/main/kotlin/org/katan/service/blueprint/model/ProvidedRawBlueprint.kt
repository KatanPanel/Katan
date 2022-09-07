package org.katan.service.blueprint.model

import org.katan.model.blueprint.RawBlueprint

data class ProvidedRawBlueprint(
    val main: ProvidedRawBlueprintMain,
    val assets: List<ProvidedRawBlueprintAsset>
)

data class ProvidedRawBlueprintMain(
    val raw: RawBlueprint,
    val name: String,
    @Suppress("ArrayInDataClass") val contents: ByteArray
)

data class ProvidedRawBlueprintAsset(
    val name: String,
    @Suppress("ArrayInDataClass") val contents: ByteArray
)