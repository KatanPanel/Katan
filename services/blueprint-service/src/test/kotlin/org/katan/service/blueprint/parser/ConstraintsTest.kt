package org.katan.service.blueprint.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.fail

class ConstraintsTest {

    @Test
    fun `required property constraint for omitted node`() {
        val input = ""
        val property = Property(
            qualifiedName = "version",
            kind = PropertyKind.Literal,
            constraints = listOf(RequiredPropertyConstraint)
        )

        val exception = assertFailsWith<ConstraintViolationException> {
            withParserTest(property) {
                read(input)
            }
        }

        assertEquals(exception.constraint, RequiredPropertyConstraint)
    }

    @Test
    fun `required property constraint for null node`() {
        val input = """
            version = null
        """.trimIndent()
        val property = Property(
            qualifiedName = "version",
            kind = PropertyKind.Mixed(
                PropertyKind.Literal,
                PropertyKind.Null /* for value type validation */
            ),
            constraints = listOf(RequiredPropertyConstraint)
        )

        try {
            withParserTest(property) {
                read(input)
            }
        } catch (e: BlueprintSpecPropertyParseException) {
            fail("Required constraint cannot consider null values", e)
        }
    }
}
