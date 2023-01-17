package org.katan.service.blueprint.parser

private val parser = BlueprintSpecParser()

internal inline fun <R> withParserTest(crossinline block: BlueprintSpecParser.() -> R): R {
    return block(parser)
}
