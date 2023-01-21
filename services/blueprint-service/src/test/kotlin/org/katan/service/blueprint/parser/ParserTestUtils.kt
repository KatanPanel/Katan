package org.katan.service.blueprint.parser

private val parser = Parser()

internal inline fun <R> withParserTest(crossinline block: Parser.() -> R): R {
    return block(parser)
}

internal inline fun <R> withParserTest(
    supportedProperties: List<Property>,
    crossinline block: Parser.() -> R
): R {
    return block(Parser(supportedProperties))
}
