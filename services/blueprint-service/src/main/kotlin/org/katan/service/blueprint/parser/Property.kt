package org.katan.service.blueprint.parser

import org.katan.model.blueprint.BlueprintSpecImage
import org.katan.service.blueprint.parser.PropertyKind.Literal
import org.katan.service.blueprint.parser.PropertyKind.Mixed
import org.katan.service.blueprint.parser.PropertyKind.Multiple
import kotlin.reflect.KClass

internal const val PROPERTY_NAME_SEPARATOR = "."

internal data class Property(
    val qualifiedName: String,
    val kind: PropertyKind,
    val constraints: List<PropertyConstraint> = listOf()
) {

    val name: String get() = qualifiedName.substringAfterLast(PROPERTY_NAME_SEPARATOR)
    val isTopLevel: Boolean get() = qualifiedName.indexOf(PROPERTY_NAME_SEPARATOR) != -1

    fun supports(kind: KClass<out PropertyKind>): Boolean {
        return supports(this.kind, kind)
    }

    companion object {
        private fun supports(kind: PropertyKind, other: KClass<out PropertyKind>): Boolean {
            return when (kind) {
                is Mixed -> kind.isAllTypesSupported || kind.kinds.any { supports(it, other) }
                is Multiple -> supports(kind.supports, other)
                else -> kind::class == other
            }
        }
    }
}

internal sealed class PropertyKind(val representation: KClass<*>) {

    object Literal : PropertyKind(String::class)
    object Numeric : PropertyKind(Number::class)
    object TrueOrFalse : PropertyKind(Boolean::class)
    data class Multiple(val supports: PropertyKind) : PropertyKind(List::class)

    data class Struct<T : Any>(val value: KClass<out T>) : PropertyKind(value)
    data class Mixed(val kinds: List<PropertyKind>) : PropertyKind(Any::class) {
        val isAllTypesSupported: Boolean get() = kinds.isEmpty()

        constructor(vararg kinds: PropertyKind) : this(kinds.toList())
    }

    object Null : PropertyKind(Nothing::class)
}

internal operator fun PropertyKind.plus(other: PropertyKind): PropertyKind {
    return if (this is Mixed) {
        Mixed(kinds.toList() + other)
    } else {
        Mixed(this, other)
    }
}

internal val AllSupportedProperties: List<Property> = listOf(
    Properties.Name,
    Properties.Version,
    Properties.Build,
    Properties.Entrypoint,
    Properties.Image
)

internal object Properties {
    val Name = Property(
        qualifiedName = "name",
        kind = Literal,
        constraints = listOf(RequiredPropertyConstraint, NotBlankPropertyConstraint)
    )
    val Version = Property(
        qualifiedName = "version",
        kind = Literal,
        constraints = listOf(RequiredPropertyConstraint, NotBlankPropertyConstraint)
    )
    val Build = Property(
        qualifiedName = "build",
        kind = PropertyKind.Struct(Any::class)
    )
    val Entrypoint = Property(
        qualifiedName = "build.entrypoint",
        kind = Literal
    )
    val Image = Property(
        qualifiedName = "build.image",
        kind = Literal + Multiple(PropertyKind.Struct(BlueprintSpecImage.Ref::class)),
        constraints = listOf(RequiredPropertyConstraint)
    )
}
