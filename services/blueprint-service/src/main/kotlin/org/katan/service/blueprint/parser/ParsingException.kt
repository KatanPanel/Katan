package org.katan.service.blueprint.parser

import org.katan.service.blueprint.BlueprintException

internal open class BlueprintSpecParsingException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : BlueprintException()

internal open class BlueprintSpecPropertyParsingException(
    val property: MixedSpecProperty,
    message: String? = null,
    cause: Throwable? = null
) : BlueprintSpecParsingException(message, cause)

internal class RequiredPropertyException(property: MixedSpecProperty, message: String?) :
    BlueprintSpecPropertyParsingException(property, message)

internal class BlankPropertyException(property: MixedSpecProperty, message: String?) :
    BlueprintSpecPropertyParsingException(property, message)
