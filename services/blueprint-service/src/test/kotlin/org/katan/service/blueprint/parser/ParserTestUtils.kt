package org.katan.service.blueprint.parser

import kotlinx.serialization.json.JsonObject

internal inline fun <R> withParserTest(crossinline block: BlueprintParser.() -> R): R {
    return block(BlueprintParser())
}

internal inline fun <R> withParserTest(
    vararg supportedProperties: Property,
    crossinline block: BlueprintParser.() -> R
): R {
    return block(BlueprintParser(supportedProperties.toList()))
}

internal fun withParserTest(input: String, vararg supportedProperties: Property): JsonObject =
    BlueprintParser(supportedProperties.toList()).read(input)
