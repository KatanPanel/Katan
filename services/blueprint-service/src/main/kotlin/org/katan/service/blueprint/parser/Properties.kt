package org.katan.service.blueprint.parser

import org.katan.service.blueprint.parser.PropertyKind.Literal
import org.katan.service.blueprint.parser.PropertyKind.Mixed
import org.katan.service.blueprint.parser.PropertyKind.Multiple
import org.katan.service.blueprint.parser.PropertyKind.Struct

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
        kind = Struct
    )
    val Entrypoint = Property(
        qualifiedName = "build.entrypoint",
        kind = Literal
    )
    val Image = Property(
        qualifiedName = "build.image",
        kind = Mixed(
            Literal,
            Multiple(Struct)
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
    val Instance = Property(
        qualifiedName = "build.instance",
        kind = Struct
    )
    val InstanceName = Property(
        qualifiedName = "build.instance.name",
        kind = Literal
    )
    val Options = Property(
        qualifiedName = "options",
        kind = Struct
    )
    val OptionsId = Property(
        qualifiedName = "options.id",
        kind = Literal,
        constraints = listOf(RequiredPropertyConstraint)
    )
    val OptionsType = Property(
        qualifiedName = "options.type",
        kind = Literal,
        constraints = listOf(RequiredPropertyConstraint)
    )
    val OptionsEnv = Property(
        qualifiedName = "options.env",
        kind = Literal,
        constraints = listOf(EnvironmentVariableConstraint)
    )
}
