package org.katan.service.blueprint.parser

import org.katan.service.blueprint.BlueprintException

internal open class BlueprintSpecParseException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : BlueprintException()

internal open class BlueprintSpecPropertyParseException(
    val property: Property,
    message: String? = null,
    cause: Throwable? = null
) : BlueprintSpecParseException(message, cause)

internal class NoMatchesForMixedProperty(message: String?, property: Property) :
    BlueprintSpecPropertyParseException(property, message)

internal class ConstraintViolationException(message: String?, property: Property, val constraint: PropertyConstraint) :
    BlueprintSpecPropertyParseException(property, message)
