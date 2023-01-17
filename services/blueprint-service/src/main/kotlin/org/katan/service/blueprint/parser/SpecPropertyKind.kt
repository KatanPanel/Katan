package org.katan.service.blueprint.parser

import kotlin.reflect.KClass

internal typealias MixedSpecProperty = SpecProperty<*>

internal data class SpecProperty<T>(
    val name: String,
    val kind: KClass<out SpecPropertyKind<T>>,
    val supportedSince: Int? = null
) {

    companion object {

        val Name = SpecProperty("name", SpecPropertyKind.StringProperty::class)
        val Version = SpecProperty("version", SpecPropertyKind.StringProperty::class)
    }
}

internal sealed interface SpecPropertyKind<T> {

    @JvmInline
    value class StringProperty(val value: String) : SpecPropertyKind<String>

    @JvmInline
    value class NumberProperty(val value: Number) : SpecPropertyKind<Number>

    @JvmInline
    value class BoolProperty(val value: Boolean) : SpecPropertyKind<Boolean>
}
