package org.katan.service.blueprint.parser

import org.katan.model.blueprint.BlueprintSpecImage
import org.katan.service.blueprint.parser.Properties.Entrypoint
import org.katan.service.blueprint.parser.Properties.Image
import org.katan.service.blueprint.parser.Properties.Name
import org.katan.service.blueprint.parser.Properties.Version
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

    fun matches(kind: PropertyKind): Boolean {
        return when (this.kind) {
            is Mixed -> this.kind.kinds.any { it == kind }
            is Multiple -> this.kind.values.any { it == kind }
            else -> this.kind == kind
        }
    }
}

internal sealed class PropertyKind(val representation: KClass<*>) {

    object Literal : PropertyKind(String::class)
    object Numeric : PropertyKind(Number::class)
    object TrueOrFalse : PropertyKind(Boolean::class)
    data class Multiple(val values: List<PropertyKind>) : PropertyKind(List::class) {
        constructor(vararg values: PropertyKind) : this(values.toList())
    }

    data class Object<T : Any>(val value: KClass<T>) : PropertyKind(value)
    data class Struct(val child: List<Property>) : PropertyKind(Any::class)
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
    Name,
    Version,
    Entrypoint,
    Image
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
    val Entrypoint = Property(
        qualifiedName = "build.entrypoint",
        kind = Literal
    )
    val Image = Property(
        qualifiedName = "build.image",
        kind = Literal + Multiple(PropertyKind.Object(BlueprintSpecImage.Ref::class)),
        constraints = listOf(RequiredPropertyConstraint)
    )
}
