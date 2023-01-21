package org.katan.service.blueprint.parser

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

internal sealed class PropertyKind {

    object Null : PropertyKind()
    object Literal : PropertyKind()
    object Numeric : PropertyKind()
    object TrueOrFalse : PropertyKind()
    object Struct : PropertyKind()
    data class Multiple(val supports: PropertyKind) : PropertyKind()
    data class Mixed(val kinds: List<PropertyKind>) : PropertyKind() {
        val isAllTypesSupported: Boolean = kinds.isEmpty()

        constructor(vararg kinds: PropertyKind) : this(kinds.toList())
    }
}

internal val AllSupportedProperties: List<Property> = listOf(
    Properties.Name,
    Properties.Version,
    Properties.Build,
    Properties.Entrypoint,
    Properties.Image,
    Properties.ImageReference,
    Properties.ImageTag
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
        kind = PropertyKind.Struct
    )
    val Entrypoint = Property(
        qualifiedName = "build.entrypoint",
        kind = Literal
    )
    val Image = Property(
        qualifiedName = "build.image",
        kind = Mixed(
            Literal,
            Multiple(PropertyKind.Struct)
        ),
        constraints = listOf(RequiredPropertyConstraint)
    )
    val ImageReference = Property(
        qualifiedName = "build.image.ref",
        kind = Literal,
        constraints = listOf(RequiredPropertyConstraint)
    )
    val ImageTag = Property(
        qualifiedName = "build.image.tag",
        kind = Literal,
        constraints = listOf(RequiredPropertyConstraint)
    )
}
