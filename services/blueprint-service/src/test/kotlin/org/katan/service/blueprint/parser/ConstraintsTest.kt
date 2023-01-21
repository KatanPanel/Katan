package org.katan.service.blueprint.parser

import org.junit.Test
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
            withParserTest(listOf(property)) {
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
            withParserTest(listOf(property)) {
                read(input)
            }
        } catch (e: BlueprintSpecPropertyParseException) {
            fail("Required constraint cannot consider null values", e)
        }
    }
}
