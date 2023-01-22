package org.katan.service.blueprint.parser

private val parser = BlueprintParser()

internal inline fun <R> withParserTest(crossinline block: BlueprintParser.() -> R): R {
    return block(parser)
}

internal inline fun <R> withParserTest(
    vararg supportedProperties: Property,
    crossinline block: BlueprintParser.() -> R
): R {
    return block(BlueprintParser(supportedProperties.toList()))
}
