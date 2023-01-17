package org.katan.service.blueprint.parser

internal object BlueprintSpecInternals {

    const val CURRENT_VERSION = 1

    @JvmField
    val supportedTypes: List<MixedSpecProperty> = listOf(
        SpecProperty("string", SpecPropertyKind.StringProperty::class),
        SpecProperty("number", SpecPropertyKind.NumberProperty::class),
        SpecProperty("bool", SpecPropertyKind.BoolProperty::class)
    )
}
